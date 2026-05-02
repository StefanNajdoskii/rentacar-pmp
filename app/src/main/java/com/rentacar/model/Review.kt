package com.rentacar.model

data class Review(
    val id: String = "",
    val bookingId: String = "",
    val carId: String = "",
    val userId: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val carBrand: String = "",
    val carModel: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
