package com.rentacar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Double,
    val imageUrl: String,
    val description: String,
    val transmission: String,
    val fuelType: String,
    val seats: Int,
    val available: Boolean,
    val location: String,
    val carType: String = "Sedan"
)
