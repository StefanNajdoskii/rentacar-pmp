package com.rentacar.cars

import android.app.Application
import androidx.lifecycle.*
import com.rentacar.data.local.AppDatabase
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.data.repository.CarRepository
import com.rentacar.model.Car
import com.rentacar.model.CarFilter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CarListViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val firestoreRepo = FirestoreRepository()
    private val carRepository = CarRepository(db.carDao(), firestoreRepo)

    private val _searchQuery = MutableStateFlow("")
    private val _filter = MutableStateFlow(CarFilter())
    private val _isLoading = MutableLiveData(true)
    private val _error = MutableLiveData<String?>()

    val isLoading: LiveData<Boolean> = _isLoading
    val error: LiveData<String?> = _error

    val cars: LiveData<List<Car>> = combine(
        _searchQuery.debounce(300),
        _filter
    ) { query, filter -> query to filter }
        .flatMapLatest { (query, filter) ->
            val base = if (query.isBlank()) carRepository.getCars()
                       else carRepository.searchCars(query)
            base.map { filter.apply(it) }
        }
        .asLiveData()

    val activeFilterCount: LiveData<Int> = _filter
        .map { it.activeCount }
        .asLiveData()

    private var syncJob: Job? = null

    init { syncCars() }

    fun syncCars() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            _isLoading.value = true
            try {
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
    fun setFilter(filter: CarFilter) { _filter.value = filter }
    fun resetFilter() { _filter.value = CarFilter() }
    fun getFilter(): CarFilter = _filter.value
    fun clearError() { _error.value = null }

    suspend fun getDistinctBrands(): List<String> = db.carDao().getDistinctBrands()
}
