package com.example.enturcase.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.enturcase.data.model.StopPlace
import com.example.enturcase.data.repository.LocationRepository
import com.example.enturcase.data.repository.StopPlacesRepository
import com.example.enturcase.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class NearbyStopsViewModel(
    private val stopPlacesRepository: StopPlacesRepository,
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
        // only using this to show the user that the list did reload
        _stopPlaces.value = emptyList()
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
            _stopPlaces.value = stopPlacesRepository.loadStopPlacesForLocation(location)
        }
    }
}

class NearbyStopsViewModelFactory(
    private val stopPlacesRepository: StopPlacesRepository,
    private val locationRepository: LocationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NearbyStopsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NearbyStopsViewModel(stopPlacesRepository, locationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
