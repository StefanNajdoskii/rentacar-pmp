package com.rentacar.cars

import android.app.Application
import androidx.lifecycle.*
import com.rentacar.data.local.AppDatabase
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.data.repository.CarRepository
import com.rentacar.model.Car
import kotlinx.coroutines.launch

class CarDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val carRepository = CarRepository(db.carDao(), FirestoreRepository())

    private val _car = MutableLiveData<Car?>()
    val car: LiveData<Car?> = _car

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCar(carId: String) = viewModelScope.launch {
        _isLoading.value = true
        _car.value = carRepository.getCarById(carId)
        _isLoading.value = false
    }
}
