package com.example.locationtracking.domain.usecase

import android.location.Location
import com.example.locationtracking.domain.model.TrackedLocation
import javax.inject.Inject

class CalculateRouteDistanceUseCase @Inject constructor() {

    operator fun invoke(locations: List<TrackedLocation>): Double {
        if (locations.size < 2) return 0.0

        var total = 0.0
        val results = FloatArray(1)
        for (i in 0 until locations.size - 1) {
            val p1 = locations[i]
            val p2 = locations[i + 1]
            Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
            total += results[0]
        }
        return total
    }
}
