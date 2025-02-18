package com.example.enturcase.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enturcase.data.repository.Departure
import com.example.enturcase.data.repository.DeparturesRepository
import com.example.enturcase.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeparturesViewModel @Inject constructor(
    private val departuresRepository: DeparturesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val stopPlaceId: String =
        savedStateHandle["stopPlaceId"] ?: throw IllegalArgumentException("Missing stopPlaceId")

    private val _departures = MutableStateFlow<List<Departure>>(listOf())
    val departures: StateFlow<List<Departure>> = _departures

    init {
        fetchDepartures()
    }

    private fun fetchDepartures() {
        Logger.debug("fetching departures for $stopPlaceId")
        viewModelScope.launch {
            val departures = departuresRepository.listDeparturesForStop(stopPlaceId)
            departures?.let {
                Logger.debug("got departures: $departures")
                _departures.value = departures
            }
        }
    }
}