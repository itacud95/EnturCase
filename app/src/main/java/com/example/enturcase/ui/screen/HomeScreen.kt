package com.example.enturcase.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.enturcase.ui.components.StopPlaceItem
import com.example.enturcase.ui.navigation.Screen
import com.example.enturcase.ui.theme.EnturCaseTheme
import com.example.enturcase.ui.viewmodel.LocationViewModel
import com.example.enturcase.ui.viewmodel.NearbyStopsViewModel
import com.example.enturcase.utils.Logger
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun LocationPermissionDialog(viewModel: LocationViewModel) {
    val isPermissionGranted by viewModel.isPermissionGranted.collectAsState()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Logger.debug("granted: $isGranted")
//        viewModel.updatePermissionStatus(isGranted)
    }

    if (!isPermissionGranted) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }) {
                    Text("Grant Permission")
                }
            },
            title = { Text("Location Permission Required") },
            text = { Text("This app requires location permission to function properly.") }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    locationViewModel: LocationViewModel
) {

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(locationPermissionState.status) {
        Logger.debug("HomeScreen LaunchedEffect")
        if (locationPermissionState.status.isGranted) {
            navController.navigate(Screen.StopPlaces.route)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Location permission is required to continue.")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
            Text("Grant Permission")
        }
    }

//    EnturCaseTheme {
//        Box {
//            Text("Home!")
//        }
//    }
}
