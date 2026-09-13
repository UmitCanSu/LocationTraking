package com.example.locationtracking.data.mapper

import android.location.Location
import com.example.locationtracking.data.model.LocationEntity
import com.example.locationtracking.domain.model.TrackedLocation


fun LocationEntity.toDomain(): TrackedLocation {
    return TrackedLocation(
        id = id,
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        accuracy = accuracy,
        timestamp = timestamp
    )
}

fun TrackedLocation.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        accuracy = accuracy,
        timestamp = timestamp
    )
}

fun Location.toDomain(): TrackedLocation {
    return TrackedLocation(
        latitude = latitude,
        longitude = longitude,
        altitude = altitude,
        speed = speed,
        accuracy = accuracy,
        timestamp = time.takeIf { it > 0 } ?: System.currentTimeMillis()
    )
}
