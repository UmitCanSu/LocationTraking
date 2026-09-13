package com.example.locationtracking.data.repository

import android.location.Location
import com.example.locationtracking.data.local.LocationDao
import com.example.locationtracking.data.mapper.toDomain
import com.example.locationtracking.data.mapper.toEntity
import com.example.locationtracking.domain.model.TrackedLocation
import com.example.locationtracking.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocationRepositoryImpl
@Inject constructor(
    private val locationDao: LocationDao
): LocationRepository{
    override fun getAllLocations(): Flow<List<TrackedLocation>> {
        return locationDao.getAllLocations().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getLastLocation(): TrackedLocation? {
        return locationDao.getLastLocation()?.toDomain()
    }

    override suspend fun saveLocationIfMoved(location: TrackedLocation): Boolean {
        val last = locationDao.getLastLocation()

        if (last == null) {
            locationDao.insertLocation(location.toEntity())
            return true
        }

        val distanceResult = FloatArray(1)
        Location.distanceBetween(
            last.latitude,
            last.longitude,
            location.latitude,
            location.longitude,
            distanceResult
        )

        val distanceMoved = distanceResult[0]

        if (distanceMoved >= MIN_DISTANCE_METERS) {
            locationDao.insertLocation(location.toEntity())
            return true
        }

        return false
    }

    override suspend fun clearRoute() {
        locationDao.clearAllLocations()
    }

    companion object {
        const val MIN_DISTANCE_METERS = 100f
    }
}