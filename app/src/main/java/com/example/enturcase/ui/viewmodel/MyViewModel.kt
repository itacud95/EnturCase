package com.example.enturcase.ui.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enturcase.data.model.Location
import com.example.enturcase.data.model.StopPlace
import com.example.enturcase.data.repository.LocationRepository
import com.example.enturcase.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MyRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    private val _data = MutableStateFlow<List<StopPlace>>(emptyList())
    val data: StateFlow<List<StopPlace>> = _data

    init {
        observeLocationUpdates()
    }

    private fun observeLocationUpdates() {
        viewModelScope.launch {
            locationRepository.getLocationUpdates()
                .collect { newLocation ->
                    if (newLocation != null) {

                        val location = Location(newLocation.latitude, newLocation.longitude)
                        _location.value = location
                        fetchData(location)
                    }
                }
        }
    }

    private fun fetchData(location: Location) {
        viewModelScope.launch {
            _data.value = repository.loadStopPlacesForLocation(location)
        }
    }
}