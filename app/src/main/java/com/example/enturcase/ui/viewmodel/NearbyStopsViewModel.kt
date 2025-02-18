package com.example.enturcase.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enturcase.data.model.Location
import com.example.enturcase.data.model.StopPlace
import com.example.enturcase.data.repository.LocationRepository
import com.example.enturcase.data.repository.StopPlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyStopsViewModel @Inject constructor(
    private val repository: StopPlacesRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    /**
     * todo: consider loading this once.
     */

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    private val _data = MutableStateFlow<List<StopPlace>>(emptyList())
    val data: StateFlow<List<StopPlace>> = _data

    val locationFlow: StateFlow<Location?> = locationRepository.getLocationUpdates()
        .map { newLocation ->
            newLocation?.let { Location(it.latitude, it.longitude) }
        }
        .onEach { location ->
            location?.let { fetchData(it) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null) // Ensures an initial value

    private fun fetchData(location: Location) {
        viewModelScope.launch {
            _data.value = repository.loadStopPlacesForLocation(location)
        }
    }
}
