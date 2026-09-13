package com.example.locationtracking.core.di

import android.content.Context
import com.example.locationtracking.data.local.LocationDatabase
import com.example.locationtracking.data.local.LocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule{
    @Provides
    @Singleton
    fun provideLocationDatabase(
        @ApplicationContext context: Context
    ): LocationDatabase {
        return LocationDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideLocationDao(
        database: LocationDatabase
    ): LocationDao {
        return database.locationDao()
    }
}