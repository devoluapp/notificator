package com.example.notificationcore.di

import com.example.notificationcore.data.local.dao.NotificationDao
import com.example.notificationcore.data.repository.NotificationRepositoryImpl
import com.example.notificationcore.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationDao: NotificationDao
    ): NotificationRepository {
        return NotificationRepositoryImpl(notificationDao)
    }
} 