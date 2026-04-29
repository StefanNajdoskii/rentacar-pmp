package com.rentacar.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {
    @Query("SELECT * FROM cars ORDER BY brand ASC")
    fun getAllCars(): Flow<List<CarEntity>>

    @Query("SELECT * FROM cars WHERE available = 1 ORDER BY brand ASC")
    fun getAvailableCars(): Flow<List<CarEntity>>

    @Query("SELECT * FROM cars WHERE id = :carId")
    suspend fun getCarById(carId: String): CarEntity?

    @Query("SELECT * FROM cars WHERE brand LIKE '%' || :query || '%' OR model LIKE '%' || :query || '%'")
    fun searchCars(query: String): Flow<List<CarEntity>>

    @Query("SELECT COUNT(*) FROM cars")
    suspend fun getCarCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCars(cars: List<CarEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity)

    @Delete
    suspend fun deleteCar(car: CarEntity)

    @Query("DELETE FROM cars")
    suspend fun deleteAllCars()
}
