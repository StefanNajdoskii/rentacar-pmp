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
    val pickupLocation: String,
    val paymentStatus: String,
    val createdAt: Long,
    val paymentId: String = "",
    val paymentAmount: Double = 0.0,
    val paymentDate: Long = 0L
)
