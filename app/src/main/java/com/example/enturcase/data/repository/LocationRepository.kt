package com.example.enturcase.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.enturcase.utils.Logger
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationRepository (
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    suspend fun getLocationUpdates(): Location? {
        if (!hasLocationPermission()) {
            Logger.debug("Missing location permission")
//            return null
        }

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

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}