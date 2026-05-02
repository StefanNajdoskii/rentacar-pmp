package com.rentacar.model

data class CarFilter(
    val minPrice: Double = 0.0,
    val maxPrice: Double = 500.0,
    val brand: String = "",
    val carType: String = "",
    val minYear: Int = 2000,
    val maxYear: Int = 2025,
    val transmission: String = ""
) {
    val isActive: Boolean get() =
        brand.isNotEmpty() || carType.isNotEmpty() || transmission.isNotEmpty() ||
        minPrice > 0.0 || maxPrice < 500.0 || minYear > 2000 || maxYear < 2025

    val activeCount: Int get() = listOf(
        brand.isNotEmpty(),
        carType.isNotEmpty(),
        transmission.isNotEmpty(),
        minPrice > 0.0,
        maxPrice < 500.0,
        minYear > 2000,
        maxYear < 2025
    ).count { it }

    fun apply(cars: List<Car>): List<Car> = cars.filter { car ->
        (brand.isEmpty() || car.brand.equals(brand, ignoreCase = true)) &&
        (carType.isEmpty() || car.carType.equals(carType, ignoreCase = true)) &&
        (transmission.isEmpty() || car.transmission.equals(transmission, ignoreCase = true)) &&
        car.pricePerDay >= minPrice &&
        (maxPrice >= 500.0 || car.pricePerDay <= maxPrice) &&
        car.year >= minYear &&
        car.year <= maxYear
    }
}
