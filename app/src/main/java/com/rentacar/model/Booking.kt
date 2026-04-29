package com.rentacar.model

data class Booking(
    val id: String = "",
    val userId: String = "",
    val carId: String = "",
    val carBrand: String = "",
    val carModel: String = "",
    val carImageUrl: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val totalPrice: Double = 0.0,
    val status: String = "pending",   // pending / confirmed / cancelled / completed
    val createdAt: Long = System.currentTimeMillis()
)
