package com.example.enturcase.data.model

import com.example.enturcase.type.TransportMode
import java.time.ZonedDateTime

data class Departure(
    val transportMode: TransportMode,
    val lineId: Int,
    val destination: String,
    val departure: ZonedDateTime,
)
