package com.example.enturcase.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.enturcase.domain.model.Departure
import com.example.enturcase.type.TransportMode
import com.example.enturcase.ui.components.DepartureItem
import com.example.enturcase.ui.events.UiEvent
import com.example.enturcase.ui.theme.EnturCaseTheme
import java.time.ZonedDateTime

data class DeparturesContent(
    val stopPlaceName: String,
    val departures: List<Departure>,
    val timeLeft: Map<Departure, String>, // todo: naming
)

@Preview
@Composable
fun DepartureScreenPreview() {
    DeparturesScreen(
        rememberNavController(),
        DeparturesContent(
            "Tøyen",
            listOf(
                Departure(TransportMode.bus, 123, "Skøyen",  ZonedDateTime.now()),
                Departure(TransportMode.tram, 12, "Ljabru",  ZonedDateTime.now()),
            ),
            emptyMap(),
        )
    ) { }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeparturesScreen(
    navController: NavController,
    content: DeparturesContent,
    onEvent: (UiEvent) -> Unit,
) {
    EnturCaseTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text(content.stopPlaceName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(content.departures.size) {
                            val departure = content.departures[it]
                            DepartureItem(departure, content.timeLeft[departure] ?: "--")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.popBackStack() }) {
                    Text(text = "Go Back")
                }
            }
        }
    }
}