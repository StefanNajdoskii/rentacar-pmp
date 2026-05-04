package com.rentacar.booking

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.rentacar.data.local.AppDatabase
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.data.repository.BookingRepository
import com.rentacar.data.repository.CarRepository
import com.rentacar.model.Booking
import com.rentacar.model.Car
import com.rentacar.notifications.NotificationHelper
import com.rentacar.notifications.ReturnReminderWorker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

sealed class BookingUiState {
    object Idle : BookingUiState()
    object Loading : BookingUiState()
    data class Success(val bookingId: String, val message: String) : BookingUiState()
    data class Error(val message: String) : BookingUiState()
}

class BookingViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val firestoreRepo = FirestoreRepository()
    private val bookingRepository = BookingRepository(db.bookingDao(), firestoreRepo)
    private val carRepository = CarRepository(db.carDao(), firestoreRepo)

    private val _uiState = MutableLiveData<BookingUiState>(BookingUiState.Idle)
    val uiState: LiveData<BookingUiState> = _uiState

    private val _car = MutableLiveData<Car?>()
    val car: LiveData<Car?> = _car

    private val _totalPrice = MutableLiveData(0.0)
    val totalPrice: LiveData<Double> = _totalPrice

    var startDate: Long = 0L
    var endDate: Long = 0L
    var selectedPickupLocation: String = ""

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun loadCar(carId: String) = viewModelScope.launch {
        _car.value = carRepository.getCarById(carId)
    }

    fun calculatePrice() {
        val car = _car.value ?: return
        if (startDate == 0L || endDate == 0L || endDate <= startDate) {
            _totalPrice.value = 0.0; return
        }
        val days = TimeUnit.MILLISECONDS.toDays(endDate - startDate).coerceAtLeast(1)
        _totalPrice.value = days * car.pricePerDay
    }

    fun createBooking() = viewModelScope.launch {
        val car = _car.value ?: return@launch
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

        if (startDate == 0L || endDate == 0L || endDate <= startDate) {
            _uiState.value = BookingUiState.Error("Please select valid dates"); return@launch
        }

        _uiState.value = BookingUiState.Loading
        try {
            val booking = Booking(
                userId = userId,
                carId = car.id,
                carBrand = car.brand,
                carModel = car.model,
                carImageUrl = car.imageUrl,
                startDate = startDate,
                endDate = endDate,
                totalPrice = _totalPrice.value ?: 0.0,
                status = "confirmed",
                pickupLocation = selectedPickupLocation,
                paymentStatus = "pending"
            )
            val bookingId = bookingRepository.createBooking(booking)

            // Immediate local notification for booking confirmation
            NotificationHelper.showBookingConfirmation(
                context = getApplication(),
                carName = "${car.brand} ${car.model}",
                pickupDate = dateFormat.format(Date(startDate))
            )

            // Schedule return-reminder 1 day before end date
            val reminderDelay = endDate - System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)
            if (reminderDelay > 0) {
                val workRequest = OneTimeWorkRequestBuilder<ReturnReminderWorker>()
                    .setInitialDelay(reminderDelay, TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            ReturnReminderWorker.KEY_CAR_NAME to "${car.brand} ${car.model}",
                            ReturnReminderWorker.KEY_RETURN_DATE to dateFormat.format(Date(endDate))
                        )
                    )
                    .addTag("reminder_$bookingId")
                    .build()
                WorkManager.getInstance(getApplication()).enqueue(workRequest)
            }

            _uiState.value = BookingUiState.Success(
                bookingId = bookingId,
                message = "Booking confirmed!"
            )
        } catch (e: Exception) {
            _uiState.value = BookingUiState.Error(e.message ?: "Booking failed")
        }
    }

    fun resetState() { _uiState.value = BookingUiState.Idle }
}
