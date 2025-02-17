package com.example.enturcase.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enturcase.data.model.Departure
import com.example.enturcase.type.TransportMode
import java.time.ZonedDateTime

@Preview
@Composable
fun DepartureItem(
    departure: Departure = Departure(
        TransportMode.rail,
        412,
        "Stockholm",
        ZonedDateTime.now(),
    ), remainingTime: String = "now"
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("${departure.lineId}-${departure.destination}")
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            TransportModeIcon(departure.transportMode)

            Column(
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Text(
                    text = "${departure.lineId} ${departure.destination}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = remainingTime)
            }
        }
    }
}