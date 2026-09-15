package com.example.data.repository

import com.example.data.local.TripDao
import com.example.data.model.DriverOffer
import com.example.data.model.PopularRoute
import com.example.data.model.PopularRoutesData
import com.example.data.model.RideType
import com.example.data.model.TripBookingEntity
import com.example.data.model.TripStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class TomnayaRepository(private val tripDao: TripDao) {

    val allTrips: Flow<List<TripBookingEntity>> = tripDao.getAllTrips()
    val activeTrip: Flow<TripBookingEntity?> = tripDao.getActiveTrip()

    fun getPopularRoutes(): List<PopularRoute> = PopularRoutesData.routes

    suspend fun insertTrip(trip: TripBookingEntity): Long = tripDao.insertTrip(trip)

    suspend fun updateTripStatus(tripId: Long, status: TripStatus) {
        tripDao.updateTripStatus(tripId, status.name)
    }

    suspend fun rateTrip(tripId: Long, rating: Float) {
        tripDao.rateTrip(tripId, rating)
    }

    suspend fun deleteTrip(tripId: Long) {
        tripDao.deleteTrip(tripId)
    }

    // InDrive style dynamic driver bidding simulation for Suzuki vans
    fun searchSuzukiDrivers(
        pickup: String,
        dropoff: String,
        rideType: RideType,
        offeredFare: Double,
        seatsCount: Int
    ): Flow<List<DriverOffer>> = flow {
        // First emit empty list while searching
        emit(emptyList())
        delay(1200)

        val driverPool = listOf(
            Triple("كابتن صابر الجدع", "01023456789", "ط ر ق ٥٨٢١"),
            Triple("الأسطى محروس السوزوكي", "01198765432", "س و ز ٧٧٤٢"),
            Triple("كابتن سيد أبو علي", "01234567890", "م ص ر ١٩٠٣"),
            Triple("كابتن إبراهيم شبرا", "01543219876", "ق ل ب ٤٦٢٨"),
            Triple("كابتن علاء التمناية", "01099887766", "ج ي ز ٨٣٥١")
        )

        val colors = listOf("أبيض لؤلؤي", "فضي ميتاليك", "كحلي ملكي", "أصفر تاكسي", "رمادي")

        val generatedOffers = mutableListOf<DriverOffer>()

        // Generate 3 to 4 realistic offers around or negotiating the offeredFare
        for (i in 0 until minOf(4, driverPool.size)) {
            val driver = driverPool[i]
            // Offer variation: inDrive style where driver can accept offered fare or counter-offer
            val priceVariation = when (i) {
                0 -> offeredFare // Driver accepts your proposed price directly!
                1 -> offeredFare + (if (rideType == RideType.SEAT) 2.0 else 10.0)
                2 -> maxOf(5.0, offeredFare - (if (rideType == RideType.SEAT) 1.0 else 5.0))
                else -> offeredFare + (if (rideType == RideType.SEAT) 3.0 else 15.0)
            }

            val offer = DriverOffer(
                driverId = "drv_${i + 1}",
                driverName = driver.first,
                phone = driver.second,
                rating = 4.7 + (Random.nextInt(3) / 10.0),
                totalTrips = 850 + Random.nextInt(1200),
                carPlate = driver.third,
                carModel = "سوزوكي فان 7 راكب",
                carColor = colors[i % colors.size],
                offeredPriceEgp = priceVariation,
                etaMinutes = 2 + (i * 2),
                availableSeats = if (rideType == RideType.SEAT) 7 - Random.nextInt(1, 5) else 7,
                isAirConditioned = i % 2 == 0
            )
            generatedOffers.add(offer)
            emit(generatedOffers.toList())
            delay(800)
        }
    }
}
