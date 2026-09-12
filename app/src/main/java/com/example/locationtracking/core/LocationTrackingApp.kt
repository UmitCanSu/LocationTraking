package com.example.locationtracking.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.locationtracking.data.LocationService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocationTrackingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            LocationService.CHANNEL_ID,
            "Location",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}