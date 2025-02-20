package com.example.enturcase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enturcase.domain.model.Departure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TimerViewModel : ViewModel() {
    private val _departures = MutableStateFlow<Set<Departure>>(emptySet())
    private val _timeRemaining = MutableStateFlow<Map<Departure, String>>(emptyMap())
    val timeRemaining = _timeRemaining.asStateFlow()

    init {
        startTimer()
    }

    fun addDeparture(departure: Departure) {
        _departures.update { it + departure }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                val updatedTimes = _departures.value.associateWith { getRemainingMinutes(it.departure) }
                _timeRemaining.value = updatedTimes
                delay(1_000L) // Update every 10 seconds
            }
        }
    }

    private fun getRemainingMinutes(targetTime: ZonedDateTime): String {
        val minutesLeft = Duration.between(ZonedDateTime.now(), targetTime).toMinutes()
        return when {
            minutesLeft > 60 -> ""
            minutesLeft < 0 -> "Time expired"
            minutesLeft == 0L -> "Now"
            else -> "$minutesLeft min"
        }
    }
}