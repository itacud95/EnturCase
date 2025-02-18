package com.example.enturcase.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enturcase.data.model.StopPlace
import com.example.enturcase.data.repository.LocationRepository
import com.example.enturcase.data.repository.StopPlacesRepository
import com.example.enturcase.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearbyStopsViewModel @Inject constructor(
    private val repository: StopPlacesRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    private val _stopPlaces = MutableStateFlow<List<StopPlace>>(emptyList())
    val stopPlaces: StateFlow<List<StopPlace>> = _stopPlaces

    init {
        Logger.debug("init nearby stops view model")
        refreshData()
    }

    fun refreshData() {
        fetchCurrentLocation()
    }

    private fun fetchCurrentLocation() {
        viewModelScope.launch {
            val currentLocation = locationRepository.getLocationUpdates()
            currentLocation?.let {
                Logger.debug("got location")
                _location.value = it
                fetchData(it)
            }
        }
    }

    private fun fetchData(location: Location) {
        viewModelScope.launch {
            _stopPlaces.value = repository.loadStopPlacesForLocation(location)
        }
    }
}