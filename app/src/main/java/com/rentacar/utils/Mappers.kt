package com.rentacar.utils

import com.rentacar.data.local.BookingEntity
import com.rentacar.data.local.CarEntity
import com.rentacar.data.local.ReviewEntity
import com.rentacar.model.Booking
import com.rentacar.model.Car
import com.rentacar.model.Review

fun Car.toEntity() = CarEntity(
    id, brand, model, year, pricePerDay, imageUrl, description,
    transmission, fuelType, seats, available, location, carType
)

fun CarEntity.toModel() = Car(
    id, brand, model, year, pricePerDay, imageUrl, description,
    transmission, fuelType, seats, available, location, carType
)

fun Booking.toEntity() = BookingEntity(
    id, userId, carId, carBrand, carModel, carImageUrl,
    startDate, endDate, totalPrice, status, pickupLocation, paymentStatus, createdAt
)

fun BookingEntity.toModel() = Booking(
    id = id,
    userId = userId,
    carId = carId,
    carBrand = carBrand,
    carModel = carModel,
    carImageUrl = carImageUrl,
    startDate = startDate,
    endDate = endDate,
    totalPrice = totalPrice,
    status = status,
    pickupLocation = pickupLocation,
    paymentStatus = paymentStatus,
    createdAt = createdAt
)

fun Review.toEntity() = ReviewEntity(
    id, bookingId, carId, userId, rating, comment, carBrand, carModel, createdAt
)

fun ReviewEntity.toModel() = Review(
    id, bookingId, carId, userId, rating, comment, carBrand, carModel, createdAt
)
