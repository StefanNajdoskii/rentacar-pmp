package com.rentacar.booking

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.rentacar.data.local.AppDatabase
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.data.repository.BookingRepository
import com.rentacar.data.repository.ReviewRepository
import com.rentacar.model.Booking
import com.rentacar.model.Review
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class BookingHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val firestoreRepo = FirestoreRepository()
    private val bookingRepository = BookingRepository(db.bookingDao(), firestoreRepo)
    private val reviewRepository = ReviewRepository(db.reviewDao(), firestoreRepo)
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _ratingSuccess = MutableLiveData<String?>()
    val ratingSuccess: LiveData<String?> = _ratingSuccess

    val bookings: LiveData<List<Booking>> =
        bookingRepository.getBookingsForUser(userId).asLiveData()

    // Set of booking IDs already reviewed — used to hide/show the Rate button
    private val _reviewedBookingIds = MutableLiveData<Set<String>>(emptySet())
    val reviewedBookingIds: LiveData<Set<String>> = _reviewedBookingIds

    init {
        if (userId.isNotEmpty()) {
            viewModelScope.launch {
                _isLoading.value = true
                var firstEmit = true
                try {
                    bookingRepository.syncBookingsFromFirestore(userId).collect {
                        if (firstEmit) {
                            firstEmit = false
                            bookingRepository.autoExpireBookings(userId)
                        }
                        _isLoading.value = false
                    }
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

    fun deleteBooking(bookingId: String) = viewModelScope.launch {
        try {
            bookingRepository.deleteBooking(bookingId)
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun checkReviewedStatus(bookingId: String) = viewModelScope.launch {
        val existing = reviewRepository.getReviewForBooking(bookingId)
        if (existing != null) {
            _reviewedBookingIds.value = _reviewedBookingIds.value.orEmpty() + bookingId
        }
    }

    fun submitReview(
        bookingId: String,
        carId: String,
        carBrand: String,
        carModel: String,
        rating: Float,
        comment: String
    ) = viewModelScope.launch {
        try {
            val review = Review(
                bookingId = bookingId,
                carId = carId,
                userId = userId,
                rating = rating,
                comment = comment,
                carBrand = carBrand,
                carModel = carModel
            )
            reviewRepository.addReview(review)
            _reviewedBookingIds.value = _reviewedBookingIds.value.orEmpty() + bookingId
            _ratingSuccess.value = bookingId
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    fun clearRatingSuccess() { _ratingSuccess.value = null }
    fun clearError() { _error.value = null }
}
