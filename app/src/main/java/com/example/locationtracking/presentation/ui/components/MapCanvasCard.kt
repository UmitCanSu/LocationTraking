package com.example.locationtracking.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.locationtracking.R
import com.example.locationtracking.domain.model.TrackedLocation
import com.example.locationtracking.presentation.map.MainUiState
import com.example.locationtracking.presentation.ui.theme.PrimaryBlue
import com.example.locationtracking.presentation.ui.theme.PrimaryContainerBlue
import com.example.locationtracking.presentation.ui.theme.SecondaryContainerTeal

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline

/**
 * Interactive Vector Map Canvas & HUD Overlays with Glow Polylines, Markers, and Quick Action FABs.
 */
@Composable
fun MapCanvasCard(
    uiState: MainUiState,
    cameraPositionState: CameraPositionState,
    hasLocationPermission: Boolean,
    onMarkerClick: (TrackedLocation, Int) -> Unit,
    onRecenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val startTitle = stringResource(R.string.marker_start_title)

    Card(
        modifier = modifier
            .fillMaxSize()
            .testTag("map_canvas_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Google Map Component
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapType = MapType.NORMAL,//uiState.mapType,
                    isMyLocationEnabled = hasLocationPermission
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = false,
                    myLocationButtonEnabled = false
                )
            ) {
                val points = remember(uiState.locations) {
                    uiState.locations.map { LatLng(it.latitude, it.longitude) }
                }

                // Dual Polyline: Primary outer glow + Secondary inner dash
                if (points.size >= 2) {
                    Polyline(
                        points = points,
                        color = PrimaryContainerBlue,
                        width = 12f
                    )
                    Polyline(
                        points = points,
                        color = SecondaryContainerTeal,
                        width = 6f
                    )
                }

                // Markers on tracked points
                uiState.locations.forEachIndexed { index, loc ->
                    val position = LatLng(loc.latitude, loc.longitude)
                    val isStart = index == 0
                    val isEnd = index == uiState.locations.size - 1
                    val isSelected = uiState.selectedLocation?.id == loc.id

                    val markerColor = when {
                        isSelected -> BitmapDescriptorFactory.HUE_RED
                        isStart -> BitmapDescriptorFactory.HUE_GREEN
                        isEnd && uiState.locations.size > 1 -> BitmapDescriptorFactory.HUE_VIOLET
                        else -> BitmapDescriptorFactory.HUE_AZURE
                    }

                    val title = when {
                        isStart -> startTitle
                        else -> context.getString(R.string.marker_indexed_title, index + 1, index * 100)
                    }

                    Marker(
                        state = MarkerState(position = position),
                        title = title,
                        icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                        onClick = {
                            onMarkerClick(loc, index)
                            false
                        }
                    )
                }
            }

            // Top Right Map Action Rail (Mini FABs)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Recenter My Location
                MiniMapActionButton(
                    icon = Icons.Default.MyLocation,
                    contentDescription = stringResource(R.string.cd_map_my_location),
                    iconTint = PrimaryBlue,
                    onClick = onRecenter
                )
            }

            // Live Floating Marker Pill (Selected or Live)
            if (uiState.locations.isNotEmpty()) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.95f),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                    }
                }
            }

            // Bottom Left Live Scale Legend
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.85f),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.outline)
                    )
                    Text(
                        text = stringResource(R.string.scale_legend_100m),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MiniMapActionButton(
    icon: ImageVector,
    contentDescription: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.95f),
        shadowElevation = 3.dp,
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
