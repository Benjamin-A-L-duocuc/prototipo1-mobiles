package com.example.myapplication.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationProvider(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location = suspendCancellableCoroutine { continuation ->
        val provider = when {
            hasFinePermission() && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ->
                LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ->
                LocationManager.NETWORK_PROVIDER
            else -> null
        }

        if (provider == null) {
            continuation.resumeWithException(IllegalStateException("La ubicación del dispositivo está desactivada."))
            return@suspendCancellableCoroutine
        }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationManager.removeUpdates(this)
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            }

            override fun onProviderDisabled(provider: String) {
                locationManager.removeUpdates(this)
                if (continuation.isActive) {
                    continuation.resumeWithException(IllegalStateException("El proveedor de ubicación fue desactivado."))
                }
            }

            override fun onProviderEnabled(provider: String) = Unit

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }

        continuation.invokeOnCancellation {
            locationManager.removeUpdates(listener)
        }

        try {
            locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
        } catch (error: SecurityException) {
            if (continuation.isActive) {
                continuation.resumeWithException(error)
            }
        } catch (error: IllegalArgumentException) {
            if (continuation.isActive) {
                continuation.resumeWithException(error)
            }
        }
    }

    private fun hasFinePermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}
