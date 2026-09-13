package com.example.locationtracking.domain.usecase


import com.example.locationtracking.domain.repository.LocationClient
import javax.inject.Inject

class StopLocationServiceUseCase @Inject constructor(
    private val locationClient: LocationClient,
) {
    operator fun invoke() {
        locationClient.stopLocationService()
    }
}
