package com.rentacar.booking

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.rentacar.data.local.AppDatabase
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.data.repository.BookingRepository
import com.rentacar.model.Booking
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class BookingHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val bookingRepository = BookingRepository(db.bookingDao(), FirestoreRepository())
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Room is the single source of truth for the UI; Firestore keeps it updated via the sync below.
    val bookings: LiveData<List<Booking>> =
        bookingRepository.getBookingsForUser(userId).asLiveData()

    init {
        if (userId.isNotEmpty()) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    // Persistent: collects indefinitely while ViewModel is alive.
                    // emit(Unit) from the repository signals each successful Room update.
                    bookingRepository.syncBookingsFromFirestore(userId)
                        .collect { _isLoading.value = false }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _isLoading.value = false
                    _error.value = e.message
                }
            }
        } else {
            _isLoading.value = false
        }
    }

    fun cancelBooking(bookingId: String) = viewModelScope.launch {
        try {
            bookingRepository.cancelBooking(bookingId, userId)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun clearError() { _error.value = null }
}
