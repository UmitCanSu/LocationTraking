package com.example.locationtracking.domain.usecase

import com.example.locationtracking.domain.model.TrackedLocation
import com.example.locationtracking.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(): Flow<List<TrackedLocation>> {
        return repository.getAllLocations()
    }
}
