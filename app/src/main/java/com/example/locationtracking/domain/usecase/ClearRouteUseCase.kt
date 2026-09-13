package com.example.locationtracking.domain.usecase
import com.example.locationtracking.domain.repository.LocationRepository
import javax.inject.Inject

class ClearRouteUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke() {
        repository.clearRoute()
    }
}
