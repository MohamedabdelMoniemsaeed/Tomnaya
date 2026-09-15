package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TripBookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trip_bookings ORDER BY timestamp DESC")
    fun getAllTrips(): Flow<List<TripBookingEntity>>

    @Query("SELECT * FROM trip_bookings WHERE status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY timestamp DESC LIMIT 1")
    fun getActiveTrip(): Flow<TripBookingEntity?>

    @Query("SELECT * FROM trip_bookings WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): TripBookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripBookingEntity): Long

    @Update
    suspend fun updateTrip(trip: TripBookingEntity)

    @Query("UPDATE trip_bookings SET status = :status WHERE id = :tripId")
    suspend fun updateTripStatus(tripId: Long, status: String)

    @Query("UPDATE trip_bookings SET rating = :rating WHERE id = :tripId")
    suspend fun rateTrip(tripId: Long, rating: Float)

    @Query("DELETE FROM trip_bookings WHERE id = :tripId")
    suspend fun deleteTrip(tripId: Long)
}
