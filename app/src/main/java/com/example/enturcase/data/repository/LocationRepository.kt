package com.example.enturcase.data.repository

import android.annotation.SuppressLint
import android.location.Location
import com.example.enturcase.utils.Logger
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationRepository(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    // todo: handle permissions
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return suspendCoroutine { continuation ->
            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener { e ->
                Logger.debug("Error fetching location: ${e.message}")
                continuation.resume(null)
            }
        }
    }
}