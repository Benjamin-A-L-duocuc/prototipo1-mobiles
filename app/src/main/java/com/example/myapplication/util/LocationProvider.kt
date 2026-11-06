package com.example.myapplication.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

data class LocationResult(
    val location: Location,
    val source: String,
    val fromCache: Boolean
)

class LocationProvider(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun isLocationEnabled(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            LocationManager.GPS_PROVIDER in locationManager.getProviders(true) ||
                LocationManager.NETWORK_PROVIDER in locationManager.getProviders(true)
        }

    fun getLastKnownLocation(maxAgeMs: Long = 10 * 60 * 1000L): LocationResult? {
        if (!hasPermission()) return null
        val now = System.currentTimeMillis()
        val candidates = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        ).mapNotNull { provider ->
            try {
                locationManager.getLastKnownLocation(provider)
            } catch (error: SecurityException) {
                null
            } catch (error: IllegalArgumentException) {
                null
            }
        }
        val best = candidates.maxByOrNull { it.time } ?: return null
        if (now - best.time > maxAgeMs) return null
        return LocationResult(best, best.provider.orEmpty(), true)
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LocationResult = suspendCancellableCoroutine { continuation ->
        if (!hasPermission()) {
            continuation.resumeWithException(SecurityException("Permiso de ubicación no concedido."))
            return@suspendCancellableCoroutine
        }
        if (!isLocationEnabled()) {
            continuation.resumeWithException(IllegalStateException("La ubicación del dispositivo está desactivada."))
            return@suspendCancellableCoroutine
        }

        getLastKnownLocation(2 * 60 * 1000L)?.let { cached ->
            continuation.resume(cached)
            return@suspendCancellableCoroutine
        }

        val providers = buildList {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) add(LocationManager.GPS_PROVIDER)
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) add(LocationManager.NETWORK_PROVIDER)
        }

        if (providers.isEmpty()) {
            continuation.resumeWithException(IllegalStateException("No hay proveedores de ubicación activos."))
            return@suspendCancellableCoroutine
        }

        var finished = false
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (finished) return
                finished = true
                locationManager.removeUpdates(this)
                if (continuation.isActive) {
                    continuation.resume(LocationResult(location, location.provider.orEmpty(), false))
                }
            }

            override fun onProviderDisabled(provider: String) = Unit

            override fun onProviderEnabled(provider: String) = Unit

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }

        continuation.invokeOnCancellation {
            finished = true
            locationManager.removeUpdates(listener)
        }

        try {
            providers.forEach { provider ->
                locationManager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
            }
        } catch (error: SecurityException) {
            if (continuation.isActive) continuation.resumeWithException(error)
        } catch (error: IllegalArgumentException) {
            if (continuation.isActive) continuation.resumeWithException(error)
        }
    }
}
