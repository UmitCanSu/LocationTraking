package com.example.locationtracking.domain.usecase

import android.location.Location
import android.util.Log
import com.example.locationtracking.data.mapper.toDomain
import javax.inject.Inject

class ProcessLocationUseCase
@Inject constructor(
    private val saveLocationUseCase: SaveLocationUseCase,
) {
    suspend fun invoke(location: Location)  {
        val isSavedLocation = saveLocationUseCase.invoke(location.toDomain())
        Log.e("ProcessLocationUseCase", "Location is saved: $isSavedLocation")
    }
}