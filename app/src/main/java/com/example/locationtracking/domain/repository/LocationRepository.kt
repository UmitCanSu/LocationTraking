package com.example.locationtracking.domain.repository

import com.example.locationtracking.domain.model.TrackedLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getAllLocations(): Flow<List<TrackedLocation>>
    suspend fun getLastLocation(): TrackedLocation?
    suspend fun saveLocationIfMoved(location: TrackedLocation): Boolean
    suspend fun clearRoute()
}