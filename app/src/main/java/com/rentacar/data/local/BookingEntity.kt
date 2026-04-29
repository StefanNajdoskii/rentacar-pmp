package com.rentacar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val carId: String,
    val carBrand: String,
    val carModel: String,
    val carImageUrl: String,
    val startDate: Long,
    val endDate: Long,
    val totalPrice: Double,
    val status: String,
    val createdAt: Long
)
