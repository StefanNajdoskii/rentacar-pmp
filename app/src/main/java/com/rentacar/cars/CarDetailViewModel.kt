package com.rentacar.cars

import android.app.Application
import androidx.lifecycle.*
import com.rentacar.data.local.AppDatabase
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.data.repository.CarRepository
import com.rentacar.data.repository.ReviewRepository
import com.rentacar.model.Car
import com.rentacar.model.Review
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CarDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val firestoreRepo = FirestoreRepository()
    private val carRepository = CarRepository(db.carDao(), firestoreRepo)
    private val reviewRepository = ReviewRepository(db.reviewDao(), firestoreRepo)

    private val _car = MutableLiveData<Car?>()
    val car: LiveData<Car?> = _car

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _reviews = MutableLiveData<List<Review>>(emptyList())
    val reviews: LiveData<List<Review>> = _reviews

    private val _averageRating = MutableLiveData(0f)
    val averageRating: LiveData<Float> = _averageRating

    private val _reviewCount = MutableLiveData(0)
    val reviewCount: LiveData<Int> = _reviewCount

    fun loadCar(carId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _car.value = carRepository.getCarById(carId)
            _isLoading.value = false
        }
        // Observe Room reviews and keep them synced from Firestore
        viewModelScope.launch {
            reviewRepository.getReviewsForCar(carId).collect { list ->
                _reviews.value = list
                _averageRating.value = list.map { it.rating }.average()
                    .takeIf { !it.isNaN() }?.toFloat() ?: 0f
                _reviewCount.value = list.size
            }
        }
        // Pull-sync from Firestore in background (writes into Room, which triggers the above)
        viewModelScope.launch {
            try {
                reviewRepository.syncReviewsForCar(carId).collect { }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) { /* non-critical */ }
        }
    }
}
