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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.enturcase.data.model.StopPlace
import com.example.enturcase.ui.components.StopPlaceItem
import com.example.enturcase.ui.events.UiEvent
import com.example.enturcase.ui.navigation.Screen
import com.example.enturcase.ui.theme.EnturCaseTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class StopPlacesContent(
    val stopPlaces: List<StopPlace>,
)

@Preview
@Composable
fun StopPlacesScreenPreview() {

    StopPlacesScreen(
        rememberNavController(),
        StopPlacesContent(
            stopPlaces = listOf(
                StopPlace("Ut i vår hage", "123", 0.123),
                StopPlace("Danskebåten", "123", 0.123)
            )
        ),
    ) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopPlacesScreen(
    navController: NavController,
    stopPlacesContent: StopPlacesContent,
    onEvent: (UiEvent) -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val onRefresh: () -> Unit = {
        coroutineScope.launch {
            isRefreshing = true
            onEvent(UiEvent.ReloadData)
            // todo: https://issuetracker.google.com/issues/356039090
            delay(100)
            isRefreshing = false
        }
    }

    EnturCaseTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Nearby stops") },
                    actions = {
                        IconButton(onClick = {
                            onEvent(UiEvent.ReloadData)
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload")
                        }
                    }
                )
            }
        ) { paddingValues ->

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { onRefresh() },
                state = pullToRefreshState,
                modifier = Modifier.padding(paddingValues),
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(stopPlacesContent.stopPlaces.size) {
                        val stopPlace = stopPlacesContent.stopPlaces[it]
                        StopPlaceItem(stopPlace) {
                            navController.navigate(
                                Screen.Departures.createRoute(
                                    stopPlace.source_id,
                                    stopPlace.name
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
