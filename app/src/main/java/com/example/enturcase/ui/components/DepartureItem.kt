package com.example.enturcase.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.enturcase.R
import com.example.enturcase.data.repository.Departure
import com.example.enturcase.type.TransportMode
import com.example.enturcase.ui.viewmodel.TimerViewModel
import java.time.ZonedDateTime


@Composable
fun getTransportModeIcon(mode: TransportMode): Int {
    return when (mode) {
        TransportMode.air -> R.drawable.transport_air
        TransportMode.bus -> R.drawable.transport_bus
        TransportMode.cableway -> R.drawable.transport_cableway
        TransportMode.water -> R.drawable.transport_boat
        TransportMode.funicular -> R.drawable.transport_funicular
        // todo: what kinda transportation is a lift :)
        TransportMode.lift -> R.drawable.transport_ladder
        TransportMode.rail -> R.drawable.transport_rail
        TransportMode.metro -> R.drawable.transport_metro
        TransportMode.taxi -> R.drawable.transport_taxi
        TransportMode.tram -> R.drawable.transport_tram
        TransportMode.trolleybus -> R.drawable.transport_trolleybus
        TransportMode.monorail -> R.drawable.transport_monorail
        // todo: use real icons
        TransportMode.coach -> R.drawable.transport_coach
        // must be a rocket..
        else -> R.drawable.transport_rocket
    }
}

@Composable
fun TransportModeIcon(mode: TransportMode) {
    val isDarkMode = isSystemInDarkTheme()
    val iconTint = if (isDarkMode) Color.LightGray else Color.Black

    Image(
        painter = painterResource(id = getTransportModeIcon(mode)),
        contentDescription = mode.rawValue, // Accessibility
        colorFilter = ColorFilter.tint(iconTint),
        modifier = Modifier
            .size(48.dp)
    )
}

@Composable
fun CountdownTimer(viewModel: TimerViewModel, departure: Departure) {
    val timeLeft by viewModel.timeRemaining.collectAsState()
    val remainingTime = timeLeft[departure] ?: "--:--:--"
    LaunchedEffect(departure) {
        viewModel.startTimer(departure)
    }
    Text(text = remainingTime)
}


@Preview
@Composable
fun DepartureItem(
    departure: Departure = Departure(
        TransportMode.cableway,
        20,
        "line",
        "destination",
        ZonedDateTime.now(),
    )
) {
    val viewModel: TimerViewModel = viewModel()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            TransportModeIcon(departure.transportMode)

            Column(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
            ) {
                Row {
                    Text(
                        text = departure.lineId.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = departure.destination, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
//                Text(text = departure.departure, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                CountdownTimer(viewModel, departure)
            }
        }
    }
}
