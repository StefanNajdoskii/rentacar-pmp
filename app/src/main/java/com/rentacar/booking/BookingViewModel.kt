package com.rentacar.booking

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.rentacar.data.local.AppDatabase
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.data.repository.BookingRepository
import com.rentacar.data.repository.CarRepository
import com.rentacar.model.Booking
import com.rentacar.model.Car
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

sealed class BookingUiState {
    object Idle : BookingUiState()
    object Loading : BookingUiState()
    data class Success(val message: String) : BookingUiState()
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

    fun loadCar(carId: String) = viewModelScope.launch {
        _car.value = carRepository.getCarById(carId)
    }

    fun calculatePrice() {
        val car = _car.value ?: return
        if (startDate == 0L || endDate == 0L || endDate <= startDate) { _totalPrice.value = 0.0; return }
        val days = TimeUnit.MILLISECONDS.toDays(endDate - startDate).coerceAtLeast(1)
        _totalPrice.value = days * car.pricePerDay
    }

    fun createBooking() = viewModelScope.launch {
        val car = _car.value ?: return@launch
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

        if (startDate == 0L || endDate == 0L || endDate <= startDate) {
            _uiState.value = BookingUiState.Error("Please select valid dates")
            return@launch
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
                status = "confirmed"
            )
            bookingRepository.createBooking(booking)
            _uiState.value = BookingUiState.Success("Booking confirmed!")
        } catch (e: Exception) {
            _uiState.value = BookingUiState.Error(e.message ?: "Booking failed")
        }
    }

    fun resetState() { _uiState.value = BookingUiState.Idle }
}
