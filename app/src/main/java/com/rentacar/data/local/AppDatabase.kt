package com.rentacar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CarEntity::class, BookingEntity::class, ReviewEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao
    abstract fun bookingDao(): BookingDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Extend bookings with pickup location and payment tracking
                database.execSQL(
                    "ALTER TABLE bookings ADD COLUMN pickupLocation TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE bookings ADD COLUMN paymentStatus TEXT NOT NULL DEFAULT 'pending'"
                )
                // Add car-type column for filter feature
                database.execSQL(
                    "ALTER TABLE cars ADD COLUMN carType TEXT NOT NULL DEFAULT 'Sedan'"
                )
                // Create reviews table for ratings feature
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS reviews (
                        id TEXT NOT NULL PRIMARY KEY,
                        bookingId TEXT NOT NULL,
                        carId TEXT NOT NULL,
                        userId TEXT NOT NULL,
                        rating REAL NOT NULL,
                        comment TEXT NOT NULL,
                        carBrand TEXT NOT NULL,
                        carModel TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rentacar_db"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
