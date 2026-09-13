package com.example.locationtracking.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationtracking.domain.model.TrackedLocation
import com.example.locationtracking.domain.usecase.ClearRouteUseCase
import com.example.locationtracking.domain.usecase.GetAddressUseCase
import com.example.locationtracking.domain.usecase.GetLocationsUseCase
import com.example.locationtracking.domain.usecase.StartLocationServiceUseCase
import com.example.locationtracking.domain.usecase.StopLocationServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val startLocationServiceUseCase: StartLocationServiceUseCase,
    private val stopLocationServiceUseCase: StopLocationServiceUseCase,
    private val clearRouteUseCase: ClearRouteUseCase,
    private val getAddressUseCase: GetAddressUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state

    init {
        getLocations()
    }

    private fun getLocations() {
        viewModelScope.launch {
            getLocationsUseCase.invoke().collect {
                _state.value = _state.value.copy(locations = it)
            }
        }
    }

    private fun dismissLocationDetail() {
        _state.update {
            it.copy(
                selectedLocation = null,
                selectedAddress = null,
                isLoadingAddress = false
            )
        }
    }

    private fun selectLocation(location: TrackedLocation, index: Int) {
        _state.update {
            it.copy(
                selectedLocation = location,
                selectedLocationIndex = index,
                isLoadingAddress = true,
                selectedAddress = null
            )
        }

        viewModelScope.launch {
            val address = getAddressUseCase(
                latitude = location.latitude,
                longitude = location.longitude
            )
            _state.update {
                it.copy(
                    selectedAddress = address,
                    isLoadingAddress = false
                )
            }
        }
    }

    fun processIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.StartTracking -> {
                startLocationServiceUseCase.invoke()
                _state.update { it.copy(isTracking = true) }
            }

            is MainIntent.StopTracking -> {
                stopLocationServiceUseCase.invoke()
                _state.update { it.copy(isTracking = false) }
            }

            is MainIntent.RequestResetRoute -> {
                _state.update { it.copy(showResetDialog = true) }
            }

            is MainIntent.DismissResetDialog -> {
                _state.update { it.copy(showResetDialog = false) }
            }

            is MainIntent.ConfirmResetRoute -> {
                _state.update { it.copy(showResetDialog = false) }
                viewModelScope.launch {
                    clearRouteUseCase.invoke()
                    dismissLocationDetail()
                }
            }

            is MainIntent.SelectLocation -> {
                selectLocation(intent.location, intent.index)
            }

            is MainIntent.DismissLocationDetail -> {
                dismissLocationDetail()
            }
        }
    }

}