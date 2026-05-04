package com.rentacar.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE carId = :carId ORDER BY createdAt DESC")
    fun getReviewsForCar(carId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE bookingId = :bookingId LIMIT 1")
    suspend fun getReviewForBooking(bookingId: String): ReviewEntity?

    @Query("SELECT AVG(rating) FROM reviews WHERE carId = :carId")
    suspend fun getAverageRating(carId: String): Float?

    @Query("SELECT COUNT(*) FROM reviews WHERE carId = :carId")
    suspend fun getReviewCount(carId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)
}
