package com.example.enturcase.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.enturcase.ui.components.StopPlaceItem
import com.example.enturcase.ui.navigation.Screen
import com.example.enturcase.ui.theme.EnturCaseTheme
import com.example.enturcase.ui.viewmodel.NearbyStopsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopPlacesScreen(navController: NavController, nearbyStopsViewModel: NearbyStopsViewModel) {
    val stopPlaces by nearbyStopsViewModel.stopPlaces.collectAsState()

    EnturCaseTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Stop Places") },
                    actions = {
                        IconButton(onClick = {
                            nearbyStopsViewModel.refreshData()
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(stopPlaces.size) {
                    val stopPlace = stopPlaces[it]
                    StopPlaceItem(stopPlace) {
                        navController.navigate(Screen.Departures.createRoute(stopPlace.source_id))
                    }
                }
            }
        }
    }
}
