package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CloudSyncDialog
import com.example.ui.components.DriverDashboardView
import com.example.ui.components.TomnayaHeader
import com.example.ui.screens.BookingScreen
import com.example.ui.screens.LiveTripScreen
import com.example.ui.screens.RoutesScreen
import com.example.ui.screens.TripHistoryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Slate400
import com.example.ui.theme.TomnayaGold
import com.example.ui.theme.TomnayaGoldDark
import com.example.ui.theme.TomnayaNavy
import com.example.viewmodel.AppMode
import com.example.viewmodel.PassengerTab
import com.example.viewmodel.TomnayaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TomnayaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Set Arabic RTL as primary layout direction for authentic Egyptian transport app
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    TomnayaApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TomnayaApp(viewModel: TomnayaViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tripHistory by viewModel.tripHistory.collectAsStateWithLifecycle()
    val activeTrip by viewModel.activeTrip.collectAsStateWithLifecycle()
    val cloudStatus by viewModel.cloudStatus.collectAsStateWithLifecycle()

    if (uiState.showCloudDialog) {
        CloudSyncDialog(
            status = cloudStatus,
            onDismiss = { viewModel.setShowCloudDialog(false) },
            onSyncNow = { viewModel.syncAllTripsToCloud() },
            onPhoneAuth = { phone -> viewModel.signInWithPhone(phone) }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tomnaya_main_scaffold"),
        topBar = {
            Box(modifier = Modifier.statusBarsPadding()) {
                TomnayaHeader(
                    currentMode = uiState.appMode,
                    onModeToggle = { viewModel.setAppMode(it) },
                    cloudStatus = cloudStatus,
                    onCloudClick = { viewModel.setShowCloudDialog(true) }
                )
            }
        },
        bottomBar = {
            // Only show bottom navigation in Passenger mode
            if (uiState.appMode == AppMode.PASSENGER) {
                NavigationBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("passenger_bottom_navigation"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = uiState.passengerTab == PassengerTab.BOOKING,
                        onClick = { viewModel.setPassengerTab(PassengerTab.BOOKING) },
                        icon = {
                            Icon(
                                Icons.Default.DirectionsCar,
                                contentDescription = "حجز تمناية",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("حجز تمناية", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TomnayaNavy,
                            selectedTextColor = TomnayaGoldDark,
                            indicatorColor = TomnayaGold,
                            unselectedIconColor = Slate400,
                            unselectedTextColor = Slate400
                        ),
                        modifier = Modifier.testTag("nav_tab_booking")
                    )

                    NavigationBarItem(
                        selected = uiState.passengerTab == PassengerTab.ACTIVE_TRIP,
                        onClick = { viewModel.setPassengerTab(PassengerTab.ACTIVE_TRIP) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (activeTrip != null) {
                                        Badge(containerColor = TomnayaGoldDark)
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Navigation,
                                    contentDescription = "الرحلة الحالية",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = { Text("الرحلة الحالية", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TomnayaNavy,
                            selectedTextColor = TomnayaGoldDark,
                            indicatorColor = TomnayaGold,
                            unselectedIconColor = Slate400,
                            unselectedTextColor = Slate400
                        ),
                        modifier = Modifier.testTag("nav_tab_active_trip")
                    )

                    NavigationBarItem(
                        selected = uiState.passengerTab == PassengerTab.ROUTES,
                        onClick = { viewModel.setPassengerTab(PassengerTab.ROUTES) },
                        icon = {
                            Icon(
                                Icons.Default.Route,
                                contentDescription = "خطوط السير",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("خطوط السير", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TomnayaNavy,
                            selectedTextColor = TomnayaGoldDark,
                            indicatorColor = TomnayaGold,
                            unselectedIconColor = Slate400,
                            unselectedTextColor = Slate400
                        ),
                        modifier = Modifier.testTag("nav_tab_routes")
                    )

                    NavigationBarItem(
                        selected = uiState.passengerTab == PassengerTab.HISTORY,
                        onClick = { viewModel.setPassengerTab(PassengerTab.HISTORY) },
                        icon = {
                            Icon(
                                Icons.Default.History,
                                contentDescription = "سجل الرحلات",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("سجل الرحلات", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TomnayaNavy,
                            selectedTextColor = TomnayaGoldDark,
                            indicatorColor = TomnayaGold,
                            unselectedIconColor = Slate400,
                            unselectedTextColor = Slate400
                        ),
                        modifier = Modifier.testTag("nav_tab_history")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.appMode) {
                AppMode.PASSENGER -> {
                    when (uiState.passengerTab) {
                        PassengerTab.BOOKING -> {
                            BookingScreen(
                                uiState = uiState,
                                onSeatClick = { seatNumber -> viewModel.toggleSeat(seatNumber) },
                                onRideTypeChange = { type -> viewModel.setRideType(type) },
                                onLocationsChange = { pick, drop -> viewModel.setLocations(pick, drop) },
                                onAdjustFare = { delta -> viewModel.adjustUserBid(delta) },
                                onRequestTomnaya = { viewModel.startSearchingDrivers() },
                                onCancelSearch = { viewModel.cancelSearch() },
                                onAcceptOffer = { offer -> viewModel.acceptDriverOffer(offer) }
                            )
                        }

                        PassengerTab.ACTIVE_TRIP -> {
                            LiveTripScreen(
                                activeTrip = activeTrip,
                                onCompleteTrip = { tripId -> viewModel.completeTripManually(tripId) },
                                onCancelTrip = { tripId -> viewModel.cancelTrip(tripId) },
                                onRateTrip = { tripId, rating -> viewModel.rateTrip(tripId, rating) },
                                onGoToBooking = { viewModel.setPassengerTab(PassengerTab.BOOKING) }
                            )
                        }

                        PassengerTab.ROUTES -> {
                            RoutesScreen(
                                onSelectRoute = { route -> viewModel.selectPopularRoute(route) }
                            )
                        }

                        PassengerTab.HISTORY -> {
                            TripHistoryScreen(
                                trips = tripHistory,
                                onDeleteTrip = { tripId ->
                                    viewModel.cancelTrip(tripId)
                                }
                            )
                        }
                    }
                }

                AppMode.DRIVER -> {
                    DriverDashboardView(
                        uiState = uiState,
                        onToggleOnline = { viewModel.toggleDriverOnline() },
                        onAcceptRequest = { req -> viewModel.acceptPassengerRequest(req) },
                        onRejectRequest = { reqId -> viewModel.rejectPassengerRequest(reqId) },
                        onResetSeats = { viewModel.resetDriverSeats() },
                        onUploadLicense = { uri -> viewModel.uploadDriverLicense(uri) },
                        onSimulateLicense = { viewModel.setSimulatedLicenseUploaded() }
                    )
                }
            }
        }
    }
}
