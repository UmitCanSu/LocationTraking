package com.example.locationtracking.domain.usecase

import com.example.locationtracking.domain.model.TrackedLocation
import com.example.locationtracking.domain.repository.LocationRepository
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(location: TrackedLocation): Boolean {
        return repository.saveLocationIfMoved(location)
    }
}
