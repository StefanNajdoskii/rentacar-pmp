package com.rentacar.utils

import com.rentacar.data.local.BookingEntity
import com.rentacar.data.local.CarEntity
import com.rentacar.model.Booking
import com.rentacar.model.Car

fun Car.toEntity() = CarEntity(id, brand, model, year, pricePerDay, imageUrl, description,
    transmission, fuelType, seats, available, location)

fun CarEntity.toModel() = Car(id, brand, model, year, pricePerDay, imageUrl, description,
    transmission, fuelType, seats, available, location)

fun Booking.toEntity() = BookingEntity(id, userId, carId, carBrand, carModel, carImageUrl,
    startDate, endDate, totalPrice, status, createdAt)

fun BookingEntity.toModel() = Booking(id, userId, carId, carBrand, carModel, carImageUrl,
    startDate, endDate, totalPrice, status, createdAt)
