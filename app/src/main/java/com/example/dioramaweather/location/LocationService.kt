package com.example.dioramaweather.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Service for getting device location using FusedLocationProviderClient
 */
class LocationService(private val context: Context) {

    companion object {
        private const val TAG = "LocationService"
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    /**
     * Get current location
     * Returns null if permission is not granted or location is unavailable
     */
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            Log.w(TAG, "Location permission not granted")
            return null
        }

        return try {
            // Always request current location for fresh data
            // This is more reliable on emulators where lastLocation may be stale
            val currentLocation = requestCurrentLocation()
            if (currentLocation != null) {
                currentLocation
            } else {
                // Fallback to last known location
                Log.d(TAG, "Current location null, trying last known")
                getLastKnownLocation()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get current location, trying last known", e)
            try {
                getLastKnownLocation()
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to get last known location", e2)
                null
            }
        }
    }

    @Suppress("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    Log.d(TAG, "Last known location: $location")
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to get last location", exception)
                    continuation.resume(null)
                }
        }
    }

    @Suppress("MissingPermission")
    private suspend fun requestCurrentLocation(): Location? {
        val cancellationTokenSource = CancellationTokenSource()

        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                Log.d(TAG, "Current location: $location")
                continuation.resume(location)
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Failed to get current location", exception)
                continuation.resumeWithException(exception)
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
}
