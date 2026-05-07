package com.rentacar.data.repository

import com.rentacar.data.local.BookingDao
import com.rentacar.data.remote.FirestoreRepository
import com.rentacar.model.Booking
import com.rentacar.utils.toEntity
import com.rentacar.utils.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class BookingRepository(
    private val bookingDao: BookingDao,
    private val firestoreRepository: FirestoreRepository
) {
    fun getBookingsForUser(userId: String): Flow<List<Booking>> =
        bookingDao.getBookingsForUser(userId).map { list -> list.map { it.toModel() } }

    /**
     * Writes to Room first with a locally generated UUID so the booking appears in history
     * immediately, even before Firestore confirms the write.
     */
    suspend fun createBooking(booking: Booking): String {
        val id = UUID.randomUUID().toString()
        val bookingWithId = booking.copy(id = id)
        bookingDao.insertBooking(bookingWithId.toEntity())
        firestoreRepository.createBooking(bookingWithId)
        return id
    }

    suspend fun cancelBooking(bookingId: String, userId: String) {
        firestoreRepository.cancelBooking(bookingId)
        bookingDao.getBookingById(bookingId)?.let {
            bookingDao.updateBooking(it.copy(status = "cancelled"))
        }
    }

    suspend fun deleteBooking(bookingId: String) {
        firestoreRepository.deleteBooking(bookingId)
        bookingDao.deleteBookingById(bookingId)
    }

    suspend fun autoExpireBookings(userId: String) {
        val now = System.currentTimeMillis()
        bookingDao.getBookingsForUserOnce(userId).forEach { entity ->
            if (entity.startDate < now &&
                entity.paymentStatus == "pending" &&
                entity.status != "cancelled"
            ) {
                try {
                    firestoreRepository.cancelBooking(entity.id)
                    bookingDao.updateBooking(entity.copy(status = "cancelled"))
                } catch (_: Exception) { }
            }
        }
    }

    /**
     * Keeps a live Firestore snapshot listener open for the user's bookings.
     * Each update is written to Room and a Unit is emitted so callers can track
     * when the first load completes.
     */
    fun syncBookingsFromFirestore(userId: String): Flow<Unit> = flow {
        firestoreRepository.getBookingsForUser(userId).collect { remoteBookings ->
            bookingDao.insertBookings(remoteBookings.map { it.toEntity() })
            emit(Unit)
        }
    }
}
