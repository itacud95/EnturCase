package com.example.enturcase

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.enturcase.ui.theme.EnturCaseTheme
import com.example.enturcase.utils.Logger
import com.example.enturcase.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import okhttp3.internal.format

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


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: MainViewModel by viewModels()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            getLastKnownLocation()
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
            getLastKnownLocation()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Logger.debug("Permissions are missing, requesting again")
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Logger.debug("Got location: ${location.latitude}, ${location.longitude}")
                    viewModel.fetchData(location.latitude, location.longitude)
                } else {
                    Logger.debug("Did not get location")
                }
            }
            .addOnFailureListener {
                Logger.debug("Failed to get location: ${it.message}")
            }
    }

    data class StopPlace(
        val name: String,
        val label: String,
        val distance: Double,
    )

    private fun listDeparturesForStop() {
        val stopPlaceId = "NSR:StopPlace:6547"
        val stopPlace = GraphQLClient.fetchStopPlace(stopPlaceId)
        val formattedData = StopPlaceFormatter.formatStopPlace(stopPlace)
        Logger.debug(formattedData)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.debug("oncreate")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkAndRequestPermissions()


//        listDeparturesForStop()


        viewModel.data.observe(this) { response ->
            Logger.debug("observe data: $response")
            val element = JsonParser.parseString(response)
            val json = element.asJsonObject

            val features = json.getAsJsonArray("features")
            val gson = Gson()

            for (feature in features){
                val featureObj = feature.asJsonObject
                val properties: JsonObject = featureObj.getAsJsonObject("properties") // Fix: getAsJsonObject instead of getAsJsonArray
                val stopPlace: StopPlace = gson.fromJson(properties, StopPlace::class.java)
                Logger.debug("stopPlace: $stopPlace")
            }
        }

        enableEdgeToEdge()
        setContent {
            EnturCaseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
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