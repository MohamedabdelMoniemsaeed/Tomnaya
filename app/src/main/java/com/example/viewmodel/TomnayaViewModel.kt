package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cloud.CloudSyncStatus
import com.example.data.cloud.TomnayaCloudService
import com.example.data.local.TomnayaDatabase
import com.example.data.model.DriverOffer
import com.example.data.model.PopularRoute
import com.example.data.model.PopularRoutesData
import com.example.data.model.RideType
import com.example.data.model.SeatInfo
import com.example.data.model.TripBookingEntity
import com.example.data.model.TripStatus
import com.example.data.repository.TomnayaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppMode {
    PASSENGER,
    DRIVER
}

enum class PassengerTab {
    BOOKING,
    ACTIVE_TRIP,
    ROUTES,
    HISTORY
}

data class PassengerRequestForDriver(
    val id: String,
    val passengerName: String,
    val seatsCount: Int,
    val pickup: String,
    val dropoff: String,
    val offeredPriceEgp: Double,
    val distanceKm: Double,
    val isWholeVan: Boolean = false
)

data class TomnayaUiState(
    val appMode: AppMode = AppMode.PASSENGER,
    val passengerTab: PassengerTab = PassengerTab.BOOKING,
    val pickupLocation: String = "موقف رمسيس",
    val dropoffLocation: String = "مدينة نصر (الحي العاشر)",
    val rideType: RideType = RideType.SEAT,
    val seats: List<SeatInfo> = emptyList(),
    val baseFarePerSeat: Double = 12.0,
    val baseFareWholeVan: Double = 80.0,
    val userOfferedFare: Double = 12.0,
    val isSearchingDrivers: Boolean = false,
    val driverOffers: List<DriverOffer> = emptyList(),
    val selectedOffer: DriverOffer? = null,
    // Driver mode state
    val driverIsOnline: Boolean = true,
    val driverLine: PopularRoute = PopularRoutesData.routes[0],
    val driverOccupiedSeats: Int = 3,
    val driverTodayEarnings: Double = 420.0,
    val driverCompletedTripsCount: Int = 6,
    val driverIncomingRequests: List<PassengerRequestForDriver> = emptyList(),
    val showCloudDialog: Boolean = false
)

class TomnayaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TomnayaRepository
    private val cloudService: TomnayaCloudService
    private var searchJob: Job? = null
    private var tripProgressJob: Job? = null

    private val _uiState = MutableStateFlow(TomnayaUiState(seats = generateInitialSeats()))
    val uiState: StateFlow<TomnayaUiState> = _uiState.asStateFlow()

    val tripHistory: StateFlow<List<TripBookingEntity>>
    val activeTrip: StateFlow<TripBookingEntity?>
    val cloudStatus: StateFlow<CloudSyncStatus>

    init {
        val db = TomnayaDatabase.getInstance(application)
        cloudService = TomnayaCloudService(application)
        repository = TomnayaRepository(db.tripDao(), cloudService)
        cloudStatus = repository.cloudStatus

        tripHistory = repository.allTrips
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        activeTrip = repository.activeTrip
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

        loadInitialDriverRequests()
    }

    private fun generateInitialSeats(): List<SeatInfo> {
        return listOf(
            SeatInfo(1, "الأمام (بجوار السائق)", isSelected = true, isOccupied = false, priceEgp = 12.0),
            SeatInfo(2, "الوسط (يمين الشباك)", isSelected = false, isOccupied = true, priceEgp = 12.0),
            SeatInfo(3, "الوسط (الممر الأوسط)", isSelected = false, isOccupied = false, priceEgp = 12.0),
            SeatInfo(4, "الوسط (يسار الشباك)", isSelected = false, isOccupied = true, priceEgp = 12.0),
            SeatInfo(5, "الخلف (يمين الشباك)", isSelected = false, isOccupied = false, priceEgp = 12.0),
            SeatInfo(6, "الخلف (وسط)", isSelected = false, isOccupied = false, priceEgp = 12.0),
            SeatInfo(7, "الخلف (يسار الشباك)", isSelected = false, isOccupied = false, priceEgp = 12.0)
        )
    }

    fun setAppMode(mode: AppMode) {
        _uiState.value = _uiState.value.copy(appMode = mode)
    }

    fun setPassengerTab(tab: PassengerTab) {
        _uiState.value = _uiState.value.copy(passengerTab = tab)
    }

    fun setLocations(pickup: String, dropoff: String) {
        _uiState.value = _uiState.value.copy(
            pickupLocation = pickup,
            dropoffLocation = dropoff
        )
        recalculateFare()
    }

    fun selectPopularRoute(route: PopularRoute) {
        _uiState.value = _uiState.value.copy(
            pickupLocation = route.fromStation,
            dropoffLocation = route.toStation,
            baseFarePerSeat = route.defaultSeatFareEgp,
            baseFareWholeVan = route.defaultWholeVanFareEgp,
            passengerTab = PassengerTab.BOOKING
        )
        recalculateFare()
    }

    fun setRideType(type: RideType) {
        _uiState.value = _uiState.value.copy(rideType = type)
        recalculateFare()
    }

    fun toggleSeat(seatNumber: Int) {
        val currentSeats = _uiState.value.seats.map { seat ->
            if (seat.seatNumber == seatNumber && !seat.isOccupied) {
                seat.copy(isSelected = !seat.isSelected)
            } else {
                seat
            }
        }
        _uiState.value = _uiState.value.copy(seats = currentSeats)
        recalculateFare()
    }

    private fun recalculateFare() {
        val state = _uiState.value
        val newFare = if (state.rideType == RideType.WHOLE_VAN) {
            state.baseFareWholeVan
        } else {
            val selectedCount = maxOf(1, state.seats.count { it.isSelected })
            state.baseFarePerSeat * selectedCount
        }
        _uiState.value = _uiState.value.copy(userOfferedFare = newFare)
    }

    fun adjustUserBid(delta: Double) {
        val current = _uiState.value.userOfferedFare
        val minPrice = if (_uiState.value.rideType == RideType.WHOLE_VAN) 40.0 else 5.0
        val updated = maxOf(minPrice, current + delta)
        _uiState.value = _uiState.value.copy(userOfferedFare = updated)
    }

    fun startSearchingDrivers() {
        searchJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isSearchingDrivers = true,
            driverOffers = emptyList()
        )

        searchJob = viewModelScope.launch {
            val state = _uiState.value
            val seatsCount = if (state.rideType == RideType.WHOLE_VAN) 7 else state.seats.count { it.isSelected }

            repository.searchSuzukiDrivers(
                pickup = state.pickupLocation,
                dropoff = state.dropoffLocation,
                rideType = state.rideType,
                offeredFare = state.userOfferedFare,
                seatsCount = maxOf(1, seatsCount)
            ).collect { offers ->
                _uiState.value = _uiState.value.copy(driverOffers = offers)
            }
        }
    }

    fun cancelSearch() {
        searchJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isSearchingDrivers = false,
            driverOffers = emptyList()
        )
    }

    fun acceptDriverOffer(offer: DriverOffer) {
        searchJob?.cancel()
        viewModelScope.launch {
            val state = _uiState.value
            val selectedSeatsDesc = if (state.rideType == RideType.WHOLE_VAN) {
                "سوزوكي كاملة (7 كراسي مخصوص)"
            } else {
                val seatNums = state.seats.filter { it.isSelected }.map { it.seatNumber }
                "كراسي: ${seatNums.joinToString(", ")}"
            }

            val entity = TripBookingEntity(
                pickupLocation = state.pickupLocation,
                dropoffLocation = state.dropoffLocation,
                rideType = state.rideType.name,
                selectedSeatsCount = if (state.rideType == RideType.WHOLE_VAN) 7 else state.seats.count { it.isSelected },
                selectedSeatsDesc = selectedSeatsDesc,
                farePriceEgp = offer.offeredPriceEgp,
                driverName = offer.driverName,
                driverPhone = offer.phone,
                carPlate = offer.carPlate,
                carColor = offer.carColor,
                status = TripStatus.ACCEPTED.name
            )

            val tripId = repository.insertTrip(entity)

            _uiState.value = _uiState.value.copy(
                isSearchingDrivers = false,
                driverOffers = emptyList(),
                selectedOffer = offer,
                passengerTab = PassengerTab.ACTIVE_TRIP
            )

            startTripLifecycleSimulation(tripId)
        }
    }

    private fun startTripLifecycleSimulation(tripId: Long) {
        tripProgressJob?.cancel()
        tripProgressJob = viewModelScope.launch {
            // ACCEPTED -> ARRIVING
            delay(5000)
            repository.updateTripStatus(tripId, TripStatus.ARRIVING)

            // ARRIVING -> ON_TRIP
            delay(6000)
            repository.updateTripStatus(tripId, TripStatus.ON_TRIP)

            // ON_TRIP -> COMPLETED (can also be manually completed)
            delay(12000)
            repository.updateTripStatus(tripId, TripStatus.COMPLETED)
        }
    }

    fun completeTripManually(tripId: Long) {
        tripProgressJob?.cancel()
        viewModelScope.launch {
            repository.updateTripStatus(tripId, TripStatus.COMPLETED)
        }
    }

    fun cancelTrip(tripId: Long) {
        tripProgressJob?.cancel()
        viewModelScope.launch {
            repository.updateTripStatus(tripId, TripStatus.CANCELLED)
        }
    }

    fun rateTrip(tripId: Long, rating: Float) {
        viewModelScope.launch {
            repository.rateTrip(tripId, rating)
        }
    }

    // Driver Mode Functions
    fun toggleDriverOnline() {
        _uiState.value = _uiState.value.copy(
            driverIsOnline = !_uiState.value.driverIsOnline
        )
    }

    fun setDriverLine(route: PopularRoute) {
        _uiState.value = _uiState.value.copy(driverLine = route)
    }

    fun acceptPassengerRequest(request: PassengerRequestForDriver) {
        val remaining = _uiState.value.driverIncomingRequests.filter { it.id != request.id }
        val updatedOccupied = minOf(7, _uiState.value.driverOccupiedSeats + request.seatsCount)
        val updatedEarnings = _uiState.value.driverTodayEarnings + request.offeredPriceEgp

        _uiState.value = _uiState.value.copy(
            driverIncomingRequests = remaining,
            driverOccupiedSeats = updatedOccupied,
            driverTodayEarnings = updatedEarnings,
            driverCompletedTripsCount = _uiState.value.driverCompletedTripsCount + 1
        )
    }

    fun rejectPassengerRequest(requestId: String) {
        val remaining = _uiState.value.driverIncomingRequests.filter { it.id != requestId }
        _uiState.value = _uiState.value.copy(driverIncomingRequests = remaining)
    }

    fun resetDriverSeats() {
        _uiState.value = _uiState.value.copy(driverOccupiedSeats = 0)
    }

    private fun loadInitialDriverRequests() {
        val sampleRequests = listOf(
            PassengerRequestForDriver(
                id = "req_1",
                passengerName = "أستاذ طارق منصور",
                seatsCount = 2,
                pickup = "موقف رمسيس - أمام مسجد الفتح",
                dropoff = "مكرم عبيد - مدينة نصر",
                offeredPriceEgp = 30.0,
                distanceKm = 12.0
            ),
            PassengerRequestForDriver(
                id = "req_2",
                passengerName = "أم كريم وأولادها",
                seatsCount = 3,
                pickup = "كوبري الفنجري",
                dropoff = "الحي العاشر",
                offeredPriceEgp = 45.0,
                distanceKm = 8.5
            ),
            PassengerRequestForDriver(
                id = "req_3",
                passengerName = "مهندس زياد نبيل",
                seatsCount = 1,
                pickup = "محطة عباسية",
                dropoff = "أول عباس العقاد",
                offeredPriceEgp = 15.0,
                distanceKm = 6.0
            ),
            PassengerRequestForDriver(
                id = "req_4",
                passengerName = "عائلة الحاج فريد (مشوار مخصوص)",
                seatsCount = 7,
                pickup = "ميدان التحرير",
                dropoff = "مول مصر - 6 أكتوبر",
                offeredPriceEgp = 180.0,
                distanceKm = 32.0,
                isWholeVan = true
            )
        )
        _uiState.value = _uiState.value.copy(driverIncomingRequests = sampleRequests)
    }

    fun setShowCloudDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCloudDialog = show)
    }

    fun syncAllTripsToCloud() {
        viewModelScope.launch {
            val trips = tripHistory.value
            for (trip in trips) {
                repository.cloudService.uploadTripToCloud(trip)
            }
        }
    }
}
