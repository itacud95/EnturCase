package com.example.enturcase

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.runBlocking

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.debug("oncreate")

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkAndRequestPermissions()

//        listDeparturesForStop()

//        lifecycleScope.launch {
//            viewModel.data.collect { response ->
//                for (stopPlace in response){
//                    Logger.debug(stopPlace.toString())
//                }
//            }
//        }

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavGraph(
                navController,
                locationViewModel,
                nearbyStopsViewModel,
//                departuresViewModel,
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EnturCaseTheme {
        Greeting("Android")
    }
}