package com.example.locationtracking.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.example.locationtracking.domain.LocationClient
import com.example.locationtracking.util.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
class DefaultLocationClient
@Inject constructor(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {
    override fun getLocationUpdates(interval: Long) = callbackFlow<Location> {
        if (!context.hasLocationPermission())
            throw LocationClient.LocationException("Missing location permission")

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            throw LocationClient.LocationException("GPS is disabled")
        }
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_MILLIS)
                .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL_MILLIS)
                .setMinUpdateDistanceMeters(MIN_DISTANCE_FILTER_METERS)
                .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.lastOrNull()?.let { location ->
                    launch {
                        send(location)
                    }
                }
            }
        }

        client.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )
        awaitClose {
            client.removeLocationUpdates(locationCallback)
        }
    }

    private companion object {
        const val UPDATE_INTERVAL_MILLIS = 5000L
        const val MIN_UPDATE_INTERVAL_MILLIS = 3000L
        const val MIN_DISTANCE_FILTER_METERS = 100f

    }
}