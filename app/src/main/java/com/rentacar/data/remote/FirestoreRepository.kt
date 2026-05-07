package com.rentacar.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rentacar.model.Booking
import com.rentacar.model.Car
import com.rentacar.model.Review
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val carsCollection = db.collection("cars")
    private val bookingsCollection = db.collection("bookings")
    private val reviewsCollection = db.collection("reviews")
    private val usersCollection = db.collection("users")

    // ── Cars ─────────────────────────────────────────────────────────────────

    fun getCars(): Flow<List<Car>> = callbackFlow {
        val listener = carsCollection
            .whereEqualTo("available", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val cars = snapshot?.documents?.mapNotNull {
                    it.toObject(Car::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(cars)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getCarById(carId: String): Car? =
        carsCollection.document(carId).get().await()
            .toObject(Car::class.java)?.copy(id = carId)

    suspend fun addCar(car: Car): String {
        val doc = if (car.id.isEmpty()) carsCollection.document()
                  else carsCollection.document(car.id)
        doc.set(car).await()
        return doc.id
    }

    // ── Bookings ──────────────────────────────────────────────────────────────

    fun getBookingsForUser(userId: String): Flow<List<Booking>> = callbackFlow {
        val listener = bookingsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val bookings = snapshot?.documents?.mapNotNull {
                    it.toObject(Booking::class.java)?.copy(id = it.id)
                }?.sortedByDescending { it.createdAt } ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createBooking(booking: Booking): String {
        val doc = if (booking.id.isEmpty()) bookingsCollection.document()
                  else bookingsCollection.document(booking.id)
        doc.set(booking).await()
        return doc.id
    }

    suspend fun cancelBooking(bookingId: String) {
        bookingsCollection.document(bookingId).update("status", "cancelled").await()
    }

    suspend fun deleteBooking(bookingId: String) {
        bookingsCollection.document(bookingId).delete().await()
    }

    suspend fun updateBookingPayment(
        bookingId: String,
        paymentStatus: String,
        paymentId: String,
        paymentAmount: Double,
        paymentDate: Long
    ) {
        bookingsCollection.document(bookingId)
            .update(mapOf(
                "paymentStatus" to paymentStatus,
                "paymentId" to paymentId,
                "paymentAmount" to paymentAmount,
                "paymentDate" to paymentDate
            )).await()
    }

    // ── Reviews ───────────────────────────────────────────────────────────────

    fun getReviewsForCar(carId: String): Flow<List<Review>> = callbackFlow {
        val listener = reviewsCollection
            .whereEqualTo("carId", carId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val reviews = snapshot?.documents?.mapNotNull {
                    it.toObject(Review::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(reviews)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addReview(review: Review): String {
        val doc = if (review.id.isEmpty()) reviewsCollection.document()
                  else reviewsCollection.document(review.id)
        doc.set(review).await()
        return doc.id
    }

    suspend fun getReviewForBooking(bookingId: String): Review? {
        val snapshot = reviewsCollection
            .whereEqualTo("bookingId", bookingId)
            .limit(1)
            .get().await()
        return snapshot.documents.firstOrNull()?.toObject(Review::class.java)
    }

    // ── User / Payment ────────────────────────────────────────────────────────

    suspend fun savePaymentMethod(userId: String, last4: String, brand: String) {
        usersCollection.document(userId)
            .set(
                mapOf("savedCard" to mapOf("last4" to last4, "brand" to brand)),
                SetOptions.merge()
            ).await()
    }

    suspend fun saveFcmToken(userId: String, token: String) {
        usersCollection.document(userId)
            .set(mapOf("fcmToken" to token), SetOptions.merge()).await()
    }

    // ── Seed data ─────────────────────────────────────────────────────────────

    suspend fun seedSampleCars() {
        val snapshot = carsCollection.limit(1).get().await()
        if (!snapshot.isEmpty) return

        val sampleCars = listOf(
            Car(id = "sample_car_1", brand = "Toyota", model = "Corolla", year = 2022,
                pricePerDay = 35.0, carType = "Sedan",
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/2019_Toyota_Corolla_sedan_%28facelift%2C_white%29%2C_front_8.28.19.jpg/1280px-2019_Toyota_Corolla_sedan_%28facelift%2C_white%29%2C_front_8.28.19.jpg",
                description = "Reliable compact sedan with excellent fuel economy.",
                transmission = "Automatic", fuelType = "Petrol", seats = 5,
                available = true, location = "Skopje"),
            Car(id = "sample_car_2", brand = "Volkswagen", model = "Golf", year = 2021,
                pricePerDay = 40.0, carType = "Hatchback",
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/35/2020_Volkswagen_Golf_Style_2.0_TDI_150_Front.jpg/1280px-2020_Volkswagen_Golf_Style_2.0_TDI_150_Front.jpg",
                description = "Popular European hatchback with sporty handling.",
                transmission = "Manual", fuelType = "Diesel", seats = 5,
                available = true, location = "Ohrid"),
            Car(id = "sample_car_3", brand = "BMW", model = "3 Series", year = 2023,
                pricePerDay = 80.0, carType = "Sedan",
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5b/2019_BMW_330i_M_Sport_%28G20%29_front_8.24.19.jpg/1280px-2019_BMW_330i_M_Sport_%28G20%29_front_8.24.19.jpg",
                description = "Luxury sports sedan with premium features.",
                transmission = "Automatic", fuelType = "Petrol", seats = 5,
                available = true, location = "Skopje"),
            Car(id = "sample_car_4", brand = "Tesla", model = "Model 3", year = 2023,
                pricePerDay = 90.0, carType = "Electric",
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/91/2019_Tesla_Model_3_Performance_AWD_%28facelifted%29%2C_front_8.27.19.jpg/1280px-2019_Tesla_Model_3_Performance_AWD_%28facelifted%29%2C_front_8.27.19.jpg",
                description = "All-electric sedan with Autopilot and long range.",
                transmission = "Automatic", fuelType = "Electric", seats = 5,
                available = true, location = "Skopje"),
            Car(id = "sample_car_5", brand = "Ford", model = "Mustang", year = 2022,
                pricePerDay = 75.0, carType = "Coupe",
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/2019_Ford_Mustang_GT_Fastback%2C_front_8.25.19.jpg/1280px-2019_Ford_Mustang_GT_Fastback%2C_front_8.25.19.jpg",
                description = "Iconic American muscle car for an unforgettable drive.",
                transmission = "Manual", fuelType = "Petrol", seats = 4,
                available = true, location = "Bitola"),
            Car(id = "sample_car_6", brand = "Mercedes-Benz", model = "C-Class", year = 2023,
                pricePerDay = 95.0, carType = "Sedan",
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6d/2022_Mercedes-Benz_C_200_Avantgarde_%28W206%29_front_8.24.22.jpg/1280px-2022_Mercedes-Benz_C_200_Avantgarde_%28W206%29_front_8.24.22.jpg",
                description = "Elegant luxury sedan with cutting-edge technology.",
                transmission = "Automatic", fuelType = "Petrol", seats = 5,
                available = true, location = "Skopje")
        )
        sampleCars.forEach { addCar(it) }
    }
}
