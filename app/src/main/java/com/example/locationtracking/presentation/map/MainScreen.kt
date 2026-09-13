package com.example.locationtracking.presentation.map

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.locationtracking.presentation.map.components.MapCanvasCard
import com.example.locationtracking.presentation.map.components.PermissionRequestBanner
import com.example.locationtracking.presentation.map.components.PrimaryActionControlsDock
import com.example.locationtracking.presentation.map.components.ResetRouteConfirmationDialog
import com.example.locationtracking.presentation.map.components.SelectedMarkerDetailDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    val permissionsToRequest = remember {
        buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.FOREGROUND_SERVICE)
        }
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = permissionsToRequest)


    val defaultCenter = remember { LatLng(41.0082, 28.9784) }
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultCenter, 14f)
    }

    // Auto-focus camera when new locations arrive
    LaunchedEffect(uiState.locations.size) {
        val last = uiState.locations.lastOrNull()
        if (last != null) {
            val target = LatLng(last.latitude, last.longitude)
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(target, 16f))
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 5.dp),
        bottomBar = {
            PrimaryActionControlsDock(
                isTracking = uiState.isTracking,
                hasPoints = uiState.locations.isNotEmpty(),
                hasPermissions = permissionsState.allPermissionsGranted,
                onStartTracking = {
                    if (permissionsState.allPermissionsGranted) {
                        viewModel.processIntent(MainIntent.StartTracking)
                    } else {
                        permissionsState.launchMultiplePermissionRequest()
                    }
                },
                onStopTracking = {
                    viewModel.processIntent(MainIntent.StopTracking)
                },
                onResetClick = {
                    viewModel.processIntent(MainIntent.RequestResetRoute)
                }
            )
        }

    ) { paddingValues ->
        Column  (
            modifier = Modifier
               .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(vertical = 5.dp),
        ) {
            // Permission Banner (if not all granted)
            if (!permissionsState.allPermissionsGranted) {
                PermissionRequestBanner(
                    onRequestPermissions = { permissionsState.launchMultiplePermissionRequest() }
                )
            }
            // 2. Interactive Google Map Card & Action FABs
            MapCanvasCard(
                uiState = uiState,
                cameraPositionState = cameraPositionState,
                hasLocationPermission = permissionsState.allPermissionsGranted,
                onMarkerClick = { loc, index ->
                    viewModel.processIntent(MainIntent.SelectLocation(loc, index))
                },
                onRecenter = {
                    coroutineScope.launch {
                        val points = uiState.locations.map { LatLng(it.latitude, it.longitude) }
                        if (points.isNotEmpty()) {
                            if (points.size == 1) {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        points.first(),
                                        16f
                                    )
                                )
                            } else {
                                val builder = LatLngBounds.builder()
                                points.forEach { builder.include(it) }
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngBounds(
                                        builder.build(),
                                        120
                                    )
                                )
                            }
                        }
                    }
                }
            )
            if (uiState.selectedLocation != null){
                uiState.selectedLocation?.let { selectedLoc ->
                    SelectedMarkerDetailDialog(
                        location = selectedLoc,
                        locationIndex = uiState.selectedLocationIndex,
                        address = uiState.selectedAddress,
                        isLoadingAddress = uiState.isLoadingAddress,
                        onDismiss = { viewModel.processIntent(MainIntent.DismissLocationDetail) },
                    )
                }
            }
        }

        // Reset Route Confirmation Dialog
        if (uiState.showResetDialog) {
            ResetRouteConfirmationDialog(
                onConfirm = { viewModel.processIntent(MainIntent.ConfirmResetRoute) },
                onDismiss = { viewModel.processIntent(MainIntent.DismissResetDialog) }
            )
        }
    }
}
