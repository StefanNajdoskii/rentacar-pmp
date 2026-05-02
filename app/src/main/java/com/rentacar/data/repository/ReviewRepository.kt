package com.rentacar.data.repository

import com.rentacar.data.local.ReviewDao
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.model.Review
import com.rentacar.utils.toEntity
import com.rentacar.utils.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ReviewRepository(
    private val reviewDao: ReviewDao,
    private val firestoreRepository: FirestoreRepository
) {

    fun getReviewsForCar(carId: String): Flow<List<Review>> =
        reviewDao.getReviewsForCar(carId).map { list -> list.map { it.toModel() } }

    suspend fun getReviewForBooking(bookingId: String): Review? =
        reviewDao.getReviewForBooking(bookingId)?.toModel()

    suspend fun addReview(review: Review): String {
        val id = UUID.randomUUID().toString()
        val withId = review.copy(id = id)
        reviewDao.insertReview(withId.toEntity())
        firestoreRepository.addReview(withId)
        return id
    }

    suspend fun getAverageRating(carId: String): Float =
        reviewDao.getAverageRating(carId) ?: 0f

    suspend fun getReviewCount(carId: String): Int =
        reviewDao.getReviewCount(carId)

    /** Keeps a live Firestore snapshot for the car's reviews synced into Room. */
    fun syncReviewsForCar(carId: String): Flow<Unit> = flow {
        firestoreRepository.getReviewsForCar(carId).collect { reviews ->
            reviewDao.insertReviews(reviews.map { it.toEntity() })
            emit(Unit)
        }
    }
}
