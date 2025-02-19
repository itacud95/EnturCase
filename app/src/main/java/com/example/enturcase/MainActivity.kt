package com.example.enturcase

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.enturcase.data.repository.LocationRepository
import com.example.enturcase.data.repository.StopPlacesRepository
import com.example.enturcase.ui.navigation.NavGraph
import com.example.enturcase.ui.theme.EnturCaseTheme
import com.example.enturcase.ui.viewmodel.LocationViewModel
import com.example.enturcase.ui.viewmodel.LocationViewModelFactory
import com.example.enturcase.ui.viewmodel.NearbyStopsViewModel
import com.example.enturcase.ui.viewmodel.NearbyStopsViewModelFactory
import com.example.enturcase.utils.Logger
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object GraphQLClient {
    private val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl("https://api.entur.io/journey-planner/v3/graphql")
        .build()

    fun fetchStopPlace(stopPlaceId: String): StopPlaceQuery.StopPlace? {
        return runBlocking {
            try {
                val response = apolloClient.query(StopPlaceQuery(stopPlaceId)).execute()

                if (response.hasErrors()) {
                    Logger.debug("GraphQL Error: ${response.errors}")
                    return@runBlocking null
                }

                return@runBlocking response.data?.stopPlace
            } catch (e: ApolloException) {
                Logger.debug("Request failed: ${e.message}")
                return@runBlocking null
            }
        }
    }
}


object StopPlaceFormatter {
    fun formatStopPlace(stopPlace: StopPlaceQuery.StopPlace?): String {
        if (stopPlace == null) {
            return "No stop place data available."
        }

        val builder = StringBuilder()
        builder.append("Stop Place: ${stopPlace.name} (ID: ${stopPlace.id})\n")
        builder.append("------------------------------------------------\n")

        val estimatedCalls = stopPlace.estimatedCalls
        if (estimatedCalls.isEmpty()) {
            builder.append("No upcoming departures found.\n")
        } else {
            builder.append("Upcoming Departures:\n\n")
            estimatedCalls.forEachIndexed { index, call ->
                builder.append("Departure ${index + 1}:\n")
                builder.append("   - Destination: ${call.destinationDisplay?.frontText ?: "Unknown"}\n")
                builder.append("   - Realtime: ${if (call.realtime) "Yes" else "No"}\n")
                builder.append("   - Aimed Arrival: ${call.aimedArrivalTime ?: "N/A"}\n")
                builder.append("   - Expected Arrival: ${call.expectedArrivalTime ?: "N/A"}\n")
                builder.append("   - Aimed Departure: ${call.aimedDepartureTime ?: "N/A"}\n")
                builder.append("   - Expected Departure: ${call.expectedDepartureTime ?: "N/A"}\n")
                builder.append("   - Quay ID: ${call.quay?.id ?: "N/A"}\n")

                val line = call.serviceJourney?.journeyPattern?.line
                if (line != null) {
                    builder.append("   - Line: ${line.name} (ID: ${line.id})\n")
                    builder.append("   - Transport Mode: ${line.transportMode}\n")
                }

                builder.append("------------------------------------------------\n")
            }
        }

        return builder.toString()
    }
}

class MainActivity : ComponentActivity() {

    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory(
            LocationRepository(
                this, LocationServices.getFusedLocationProviderClient(this)
            )
        )
    }
    private val nearbyStopsViewModel: NearbyStopsViewModel by viewModels {
        NearbyStopsViewModelFactory(
            StopPlacesRepository(),
            LocationRepository(
                this, LocationServices.getFusedLocationProviderClient(this)
            )
        )
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
//            getLastKnownLocation()
        } else {
            Logger.debug("Location permission denied")
        }
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
//            getLastKnownLocation()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun waitForPermission() {
        if (hasLocationPermission()) return // Already granted, no need to wait

        suspendCancellableCoroutine<Unit> { continuation ->
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

            // Register the callback
            permissionCallback = { granted ->
                if (granted) {
                    continuation.resume(Unit)
                } else {
                    showPermissionDialog()
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                        showPermissionDialog() // Show dialog again if the user denied
//                    } else {
//                        showSettingsDialog() // If permanently denied, direct to settings
//                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            permissionCallback?.invoke(isGranted)
            permissionCallback = null // Reset callback after use
        }

    private var permissionCallback: ((Boolean) -> Unit)? = null

    private fun fetchLocationIfPermissionGranted() {
        if (hasLocationPermission()) {
            lifecycleScope.launch {
//                val location = fetchLocation()
//                location?.let {
//                    // Handle location update (e.g., update UI or ViewModel)
//                }
            }
        }
    }

//    suspend fun fetchLocation(): Location? {
//        return suspendCancellableCoroutine { continuation ->
//            fusedLocationProviderClient.getCurrentLocation(
//                Priority.PRIORITY_HIGH_ACCURACY,
//                null
//            ).addOnSuccessListener { location ->
//                continuation.resume(location)
//            }.addOnFailureListener { e ->
//                Logger.debug("Error fetching location: ${e.message}")
//                continuation.resume(null)
//            }
//        }
//    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Needed")
            .setMessage("This app requires location access to function properly. Please grant permission or exit.")
            .setPositiveButton("Grant Permission") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Location permission is permanently denied. Please enable it in settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = android.net.Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.debug("oncreate")

//        lifecycleScope.launch {
//            waitForPermission()
//            fetchLocationIfPermissionGranted()
//        }

//        if (!hasLocationPermission()) {
//            showPermissionDialog()
//        }

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavGraph(
                navController,
                locationViewModel,
                nearbyStopsViewModel,
            )
        }
    }
}
