package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RideType(val arabicName: String) {
    SEAT("حجز كرسي (مشاركة)"),
    WHOLE_VAN("مشوار مخصوص (عربية كاملة)")
}

enum class TripStatus(val arabicName: String) {
    SEARCHING("جاري البحث عن كباتن التمناية..."),
    OFFERS_RECEIVED("عروض أسعار متاحة"),
    ACCEPTED("تم تأكيد الحجز"),
    ARRIVING("التمناية في الطريق إليك"),
    ON_TRIP("في الطريق للوجهة"),
    COMPLETED("تمت الرحلة بنجاح"),
    CANCELLED("تم الإلغاء")
}

data class SeatInfo(
    val seatNumber: Int, // 1 to 7
    val labelArabic: String,
    val isSelected: Boolean = false,
    val isOccupied: Boolean = false,
    val priceEgp: Double = 15.0
)

data class DriverOffer(
    val driverId: String,
    val driverName: String,
    val phone: String,
    val rating: Double,
    val totalTrips: Int,
    val carPlate: String,
    val carModel: String = "سوزوكي فان 7 راكب",
    val carColor: String,
    val offeredPriceEgp: Double,
    val etaMinutes: Int,
    val availableSeats: Int = 4,
    val isAirConditioned: Boolean = true
)

@Entity(tableName = "trip_bookings")
data class TripBookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pickupLocation: String,
    val dropoffLocation: String,
    val rideType: String, // SEAT or WHOLE_VAN
    val selectedSeatsCount: Int,
    val selectedSeatsDesc: String,
    val farePriceEgp: Double,
    val driverName: String,
    val driverPhone: String,
    val carPlate: String,
    val carColor: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis(),
    val rating: Float = 5.0f
)

data class PopularRoute(
    val id: String,
    val fromStation: String,
    val toStation: String,
    val defaultSeatFareEgp: Double,
    val defaultWholeVanFareEgp: Double,
    val estimatedDistanceKm: Double,
    val estimatedTimeMin: Int,
    val frequentTimes: String
)
