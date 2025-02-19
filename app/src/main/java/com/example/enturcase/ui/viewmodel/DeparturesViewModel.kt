package com.example.enturcase.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enturcase.data.repository.Departure
import com.example.enturcase.data.repository.DeparturesRepository
import com.example.enturcase.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class DeparturesViewModel @Inject constructor(
    private val departuresRepository: DeparturesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val stopPlaceId: String =
        savedStateHandle["stopPlaceId"] ?: throw IllegalArgumentException("Missing stopPlaceId")

    private val _departures = MutableStateFlow<List<Departure>>(emptyList())
    val departures: StateFlow<List<Departure>> = _departures

    private val _timeRemaining = MutableStateFlow<Map<Departure, String>>(emptyMap())
    val timeRemaining = _timeRemaining.asStateFlow()

    init {
        fetchDepartures()
        startTimer()
    }

//    fun reload() {
//        fetchDepartures()
//        startTimer()
//    }

    private fun fetchDepartures() {
        Logger.debug("Fetching departures for $stopPlaceId")
        viewModelScope.launch {
            val departures = departuresRepository.listDeparturesForStop(stopPlaceId)
            departures.let {
                Logger.debug("Got departures: $departures")
                _departures.value = departures
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                val updatedTimes = _departures.value.associateWith { departure ->
                    getRemainingMinutes(departure.departure)
                }

                _timeRemaining.value = updatedTimes

                // Remove expired departures and refresh list
                if (updatedTimes.values.contains("Time expired")) {
                    _departures.update { departures ->
                        departures.filterNot { getRemainingMinutes(it.departure) == "Time expired" }
                    }
                    fetchDepartures() // Fetch new departures when any expires
                }

                delay(1_000L) // Update every second
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