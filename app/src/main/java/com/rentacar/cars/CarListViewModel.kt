package com.rentacar.cars

import android.app.Application
import androidx.lifecycle.*
import com.rentacar.data.local.AppDatabase
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.data.repository.CarRepository
import com.rentacar.model.Car
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CarListViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val firestoreRepo = FirestoreRepository()
    private val carRepository = CarRepository(db.carDao(), firestoreRepo)

    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableLiveData(true)
    private val _error = MutableLiveData<String?>()

    val isLoading: LiveData<Boolean> = _isLoading
    val error: LiveData<String?> = _error

    // Room is the single source of truth for the UI; Firestore keeps it updated via syncCars().
    val cars: LiveData<List<Car>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) carRepository.getCars()
            else carRepository.searchCars(query)
        }
        .asLiveData()

    private var syncJob: Job? = null

    init { syncCars() }

    fun syncCars() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                // Persistent: collects indefinitely while ViewModel is alive.
                // emit(Unit) from the repository signals each successful Room update.
                carRepository.syncCarsFromFirestore()
                    .collect { _isLoading.value = false }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = e.message
            }
        }
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun clearError() { _error.value = null }
}
