package com.example.locationtracking.core.di

import com.example.locationtracking.data.local.LocationDao
import com.example.locationtracking.data.repository.LocationRepositoryImpl
import com.example.locationtracking.domain.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationRepositoryModule {
    @Provides
    @Singleton
    fun provideLocationRepository(
        locationDao: LocationDao,
    ): LocationRepository {
        return LocationRepositoryImpl(locationDao)
    }
}