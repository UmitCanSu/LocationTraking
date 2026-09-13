package com.example.locationtracking.presentation.map

import com.example.locationtracking.domain.model.TrackedLocation

data class MainUiState(
    val locations: List<TrackedLocation> = emptyList(),
    val isTracking: Boolean = false,
    val totalDistanceMeters: Double = 0.0,
    val selectedLocation: TrackedLocation? = null,
    val selectedLocationIndex: Int = 0,
    val selectedAddress: String? = null,
    val isLoadingAddress: Boolean = false,
    val showResetDialog: Boolean = false,
)
