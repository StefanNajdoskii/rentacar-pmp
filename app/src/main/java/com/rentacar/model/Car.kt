package com.rentacar.model

data class Car(
    val id: String = "",
    val brand: String = "",
    val model: String = "",
    val year: Int = 0,
    val pricePerDay: Double = 0.0,
    val imageUrl: String = "",
    val description: String = "",
    val transmission: String = "",   // "Automatic" / "Manual"
    val fuelType: String = "",       // "Petrol" / "Diesel" / "Electric"
    val seats: Int = 5,
    val available: Boolean = true,
    val location: String = ""
)
