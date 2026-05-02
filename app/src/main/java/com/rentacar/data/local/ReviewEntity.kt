package com.rentacar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val bookingId: String,
    val carId: String,
    val userId: String,
    val rating: Float,
    val comment: String,
    val carBrand: String,
    val carModel: String,
    val createdAt: Long
)
