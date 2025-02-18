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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.enturcase.ui.components.StopPlaceItem
import com.example.enturcase.ui.navigation.Screen
import com.example.enturcase.ui.theme.EnturCaseTheme
import com.example.enturcase.ui.viewmodel.NearbyStopsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: NearbyStopsViewModel = hiltViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val locationFlow = remember(viewModel, lifecycleOwner) {
        viewModel.locationFlow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
    }

    val location by locationFlow.collectAsState(initial = null)
//    val data by viewModel.data.collectAsState()

    val stopPlaces by viewModel.data.collectAsState()

    EnturCaseTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Stop Places") },
                    actions = {
                        IconButton(onClick = {
//                        viewModel.refreshData()
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
                        navController.navigate(Screen.Details.createRoute(stopPlace.source_id)) }
                }
            }
        }
    }
}
