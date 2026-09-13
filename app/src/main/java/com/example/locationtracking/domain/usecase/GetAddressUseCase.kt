package com.example.locationtracking.domain.usecase
import android.content.Context
import com.example.locationtracking.util.GeocoderHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): String {
       return GeocoderHelper.getAddressDescription(context, latitude, longitude)
    }
}
