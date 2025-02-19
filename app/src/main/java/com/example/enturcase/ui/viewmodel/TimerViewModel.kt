package com.example.enturcase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enturcase.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TimerViewModel : ViewModel() {
    private val _timeRemaining = MutableStateFlow<Map<String, String>>(emptyMap())
    val timeRemaining = _timeRemaining.asStateFlow()

    fun startTimer(targetTime: String) {
        viewModelScope.launch {
            while (true) {
                val remaining = getRemainingTime(targetTime)
                _timeRemaining.update { it + (targetTime to remaining) }
                kotlinx.coroutines.delay(1000L) // Update every second
            }
        }
    }

    private fun getRemainingTime(targetTime: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            val targetDateTime = ZonedDateTime.parse(targetTime, formatter)
            val now = ZonedDateTime.now()

            val duration = Duration.between(now, targetDateTime)

            if (duration.isNegative) {
                "Time expired"
            } else {
                val hours = duration.toHours()
                val minutes = duration.toMinutes() % 60
                val seconds = duration.seconds % 60
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }
        } catch (e: Exception) {
            "Invalid date"
        }
    }
}