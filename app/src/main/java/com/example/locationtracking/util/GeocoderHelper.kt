package com.example.locationtracking.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

object GeocoderHelper {

    suspend fun getAddressDescription(context: Context, latitude: Double, longitude: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!Geocoder.isPresent()) {
                    return@withContext "Koordinat: %.5f, %.5f".format(Locale.getDefault(), latitude, longitude)
                }

                val geocoder = Geocoder(context, Locale.getDefault())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                val formatted = formatAddressList(addresses, latitude, longitude)
                                continuation.resume(formatted)
                            }

                            override fun onError(errorMessage: String?) {
                                continuation.resume("Koordinat: %.5f, %.5f".format(Locale.getDefault(), latitude, longitude))
                            }
                        })
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    formatAddressList(addresses, latitude, longitude)
                }
            } catch (e: Exception) {
                "Koordinat: %.5f, %.5f".format(Locale.getDefault(), latitude, longitude)
            }
        }
    }

    private fun formatAddressList(addresses: List<Address>?, latitude: Double, longitude: Double): String {
        val address = addresses?.firstOrNull() ?: return "Koordinat: %.5f, %.5f".format(Locale.getDefault(), latitude, longitude)

        val parts = mutableListOf<String>()
        address.thoroughfare?.let { thoroughfare ->
            val subThoroughfare = address.subThoroughfare
            if (subThoroughfare != null) {
                parts.add("$thoroughfare No: $subThoroughfare")
            } else {
                parts.add(thoroughfare)
            }
        }

        address.subLocality?.let { parts.add(it) }
        address.locality?.let { parts.add(it) }
        address.adminArea?.let { parts.add(it) }
        address.countryName?.let { parts.add(it) }

        return if (parts.isNotEmpty()) {
            parts.joinToString(", ")
        } else {
            address.getAddressLine(0) ?: "Koordinat: %.5f, %.5f".format(Locale.getDefault(), latitude, longitude)
        }
    }
}
