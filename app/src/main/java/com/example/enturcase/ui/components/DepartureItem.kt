package com.example.enturcase.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.enturcase.data.repository.Departure

@Composable
fun DepartureItem(departure: Departure) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column (modifier = Modifier.padding(16.dp)) {
            Text(text = departure.transportMode, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = departure.destination, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = departure.line, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = departure.departure, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
