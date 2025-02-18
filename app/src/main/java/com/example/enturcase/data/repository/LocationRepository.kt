package com.example.enturcase.data.repository

import android.location.Location
import com.example.enturcase.data.provider.LocationProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationProvider: LocationProvider
) {

    fun getLocationUpdates(): Flow<Location?> {
        return locationProvider.getLocationUpdates()
    }
}