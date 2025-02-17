package com.example.enturcase.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.enturcase.R
import com.example.enturcase.type.TransportMode

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