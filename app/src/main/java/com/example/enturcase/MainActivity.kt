package com.example.enturcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.example.enturcase.data.repository.LocationRepository
import com.example.enturcase.data.repository.StopPlacesRepository
import com.example.enturcase.ui.navigation.NavGraph
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

class MainActivity : ComponentActivity() {
    private val nearbyStopsViewModel: NearbyStopsViewModel by viewModels {
        NearbyStopsViewModelFactory(
            StopPlacesRepository(),
            LocationRepository(
                this, LocationServices.getFusedLocationProviderClient(this)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.debug("oncreate")

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavGraph(
                navController,
                nearbyStopsViewModel,
            )
        }
    }
}
