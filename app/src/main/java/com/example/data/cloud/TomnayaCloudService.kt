package com.example.data.cloud

import android.content.Context
import android.util.Log
import com.example.data.model.TripBookingEntity
import com.example.data.model.TripStatus
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class CloudSyncStatus(
    val isConnected: Boolean,
    val statusMessage: String,
    val pendingUploadsCount: Int = 0,
    val lastSyncTime: Long? = null
)

class TomnayaCloudService(context: Context) {

    private val tag = "TomnayaCloud"
    private var firestore: FirebaseFirestore? = null

    private val _syncStatus = MutableStateFlow(
        CloudSyncStatus(
            isConnected = false,
            statusMessage = "جاري تهيئة الاتصال السحابي..."
        )
    )
    val syncStatus: StateFlow<CloudSyncStatus> = _syncStatus.asStateFlow()

    init {
        try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                firestore = FirebaseFirestore.getInstance()
                _syncStatus.value = CloudSyncStatus(
                    isConnected = true,
                    statusMessage = "متصل بسحابة Firebase بنجاح ☁️",
                    lastSyncTime = System.currentTimeMillis()
                )
                Log.d(tag, "Firebase Firestore initialized successfully.")
            } else {
                _syncStatus.value = CloudSyncStatus(
                    isConnected = false,
                    statusMessage = "جاهز للمزامنة السحابية (بانتظار google-services.json) ☁️"
                )
                Log.w(tag, "Firebase not yet initialized. Operating in local cache fallback mode.")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize Firestore: ${e.message}", e)
            _syncStatus.value = CloudSyncStatus(
                isConnected = false,
                statusMessage = "وضع التخزين المؤقت المحلي (سيعاد التزامن عند توفر الاتصال)"
            )
        }
    }

    /**
     * Upload or update a trip in Cloud Firestore.
     */
    suspend fun uploadTripToCloud(trip: TripBookingEntity): Boolean {
        val db = firestore ?: return false
        return suspendCancellableCoroutine { continuation ->
            try {
                val tripDoc = mapOf(
                    "id" to trip.id,
                    "pickupLocation" to trip.pickupLocation,
                    "dropoffLocation" to trip.dropoffLocation,
                    "rideType" to trip.rideType,
                    "seatsCount" to trip.selectedSeatsCount,
                    "seatNumbers" to trip.selectedSeatsDesc,
                    "fareEgp" to trip.farePriceEgp,
                    "driverName" to trip.driverName,
                    "driverPhone" to trip.driverPhone,
                    "carPlate" to trip.carPlate,
                    "carColor" to trip.carColor,
                    "status" to trip.status,
                    "timestamp" to trip.timestamp,
                    "userRating" to trip.rating,
                    "lastCloudSync" to System.currentTimeMillis()
                )

                db.collection("tomnaya_trips")
                    .document(trip.id.toString())
                    .set(tripDoc, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(tag, "Trip ${trip.id} uploaded to Cloud Firestore.")
                        _syncStatus.value = _syncStatus.value.copy(
                            lastSyncTime = System.currentTimeMillis(),
                            statusMessage = "تمت مزامنة الرحلة مع السحابة ☁️✓"
                        )
                        if (continuation.isActive) continuation.resume(true)
                    }
                    .addOnFailureListener { ex ->
                        Log.e(tag, "Cloud upload failed for trip ${trip.id}: ${ex.message}")
                        if (continuation.isActive) continuation.resume(false)
                    }
            } catch (e: Exception) {
                Log.e(tag, "Unexpected error uploading to cloud", e)
                if (continuation.isActive) continuation.resume(false)
            }
        }
    }

    /**
     * Update trip status or user rating in Cloud Firestore.
     */
    suspend fun updateTripStatusInCloud(tripId: Long, status: TripStatus, rating: Float? = null): Boolean {
        val db = firestore ?: return false
        return suspendCancellableCoroutine { continuation ->
            try {
                val updates = mutableMapOf<String, Any>(
                    "status" to status.name,
                    "lastCloudSync" to System.currentTimeMillis()
                )
                if (rating != null) {
                    updates["userRating"] = rating
                }

                db.collection("tomnaya_trips")
                    .document(tripId.toString())
                    .update(updates)
                    .addOnSuccessListener {
                        if (continuation.isActive) continuation.resume(true)
                    }
                    .addOnFailureListener {
                        if (continuation.isActive) continuation.resume(false)
                    }
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(false)
            }
        }
    }

    /**
     * Real-time listener for cloud driver occupancy updates (Line / Route sync).
     */
    fun listenToRouteOccupancy(routeId: String): Flow<Int?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }

        val listener = db.collection("active_routes")
            .document(routeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(tag, "Error listening to route: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val occupied = snapshot.getLong("occupiedSeats")?.toInt()
                    trySend(occupied)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Update driver line status in Cloud (e.g. 4/7 seats occupied).
     */
    suspend fun updateDriverLineInCloud(
        routeId: String,
        driverName: String,
        occupiedSeats: Int,
        isOnline: Boolean
    ): Boolean {
        val db = firestore ?: return false
        return suspendCancellableCoroutine { continuation ->
            try {
                val data = mapOf(
                    "routeId" to routeId,
                    "driverName" to driverName,
                    "occupiedSeats" to occupiedSeats,
                    "totalSeats" to 7,
                    "isOnline" to isOnline,
                    "updatedAt" to System.currentTimeMillis()
                )
                db.collection("active_routes")
                    .document(routeId)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        if (continuation.isActive) continuation.resume(true)
                    }
                    .addOnFailureListener {
                        if (continuation.isActive) continuation.resume(false)
                    }
            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(false)
            }
        }
    }
}
