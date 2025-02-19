package com.example.enturcase.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.enturcase.data.repository.Departure
import com.example.enturcase.data.repository.DeparturesRepository
import com.example.enturcase.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.ZonedDateTime

class DeparturesViewModelFactory(
    private val departuresRepository: DeparturesRepository,
    private val stopPlaceId: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeparturesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeparturesViewModel(departuresRepository, stopPlaceId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DeparturesViewModel(
    private val departuresRepository: DeparturesRepository,
    private val stopPlaceId: String,
//    savedStateHandle: SavedStateHandle
) : ViewModel() {

//    private val stopPlaceId: String = ""
//        savedStateHandle["stopPlaceId"] ?: throw IllegalArgumentException("Missing stopPlaceId")

    private val _departures = MutableStateFlow<List<Departure>>(emptyList())
    val departures: StateFlow<List<Departure>> = _departures.asStateFlow()

    private val _timeRemaining = MutableStateFlow<Map<Departure, String>>(emptyMap())
    val timeRemaining: StateFlow<Map<Departure, String>> = _timeRemaining.asStateFlow()

    private val timerJob = MutableStateFlow<Job?>(null)

    init {
        fetchDepartures()
        startTimer()
    }

    private fun fetchDepartures() {
        Logger.debug("Fetching departures for $stopPlaceId")
        viewModelScope.launch(Dispatchers.IO) {
            val departures = departuresRepository.listDeparturesForStop(stopPlaceId)
            withContext(Dispatchers.Main) {
                _departures.value = departures
                Logger.debug("Got departures: $departures")
            }
        }
    }

    private fun startTimer() {
        timerJob.value?.cancel()
        timerJob.value = viewModelScope.launch {
            while (isActive) {
                val updatedTimes = _departures.value.associateWith { departure ->
                    getRemainingMinutes(departure.departure)
                }

                _timeRemaining.value = updatedTimes

                // Remove expired departures and refresh list
                val hasExpired = updatedTimes.values.contains("Time expired")
                if (hasExpired) {
                    _departures.update { departures ->
                        departures.filterNot { getRemainingMinutes(it.departure) == "Time expired" }
                    }
                    fetchDepartures()
                }

                delay(1_000L)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob.value?.cancel()
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