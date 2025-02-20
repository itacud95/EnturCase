package com.example.enturcase.ui.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.enturcase.data.repository.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    private val _isPermissionGranted = MutableStateFlow(locationRepository.hasLocationPermission())
    val isPermissionGranted: StateFlow<Boolean> get() = _isPermissionGranted.asStateFlow()


    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> get() = _location

    fun fetchLocation() {
        viewModelScope.launch {
            val locationResult = locationRepository.getLocationUpdates()
            _location.postValue(locationResult)
        }
    }
}

class LocationViewModelFactory(private val locationRepository: LocationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(locationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
