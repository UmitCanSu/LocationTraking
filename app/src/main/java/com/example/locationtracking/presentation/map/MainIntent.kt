package com.example.locationtracking.presentation.map

import com.example.locationtracking.domain.model.TrackedLocation

sealed interface MainIntent {
    data object StartTracking : MainIntent
    data object StopTracking : MainIntent
    data object RequestResetRoute : MainIntent
    data object ConfirmResetRoute : MainIntent
    data object DismissResetDialog : MainIntent
    data class SelectLocation(val location: TrackedLocation, val index: Int) : MainIntent
    data object DismissLocationDetail : MainIntent
}
