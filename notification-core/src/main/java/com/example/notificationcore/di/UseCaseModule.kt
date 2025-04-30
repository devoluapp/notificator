package com.example.notificationcore.di

import com.example.notificationcore.domain.repository.NotificationRepository
import com.example.notificationcore.domain.usecase.GetNotificationsUseCase
import com.example.notificationcore.domain.usecase.SaveNotificationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetNotificationsUseCase(
        repository: NotificationRepository
    ): GetNotificationsUseCase {
        return GetNotificationsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveNotificationUseCase(
        repository: NotificationRepository
    ): SaveNotificationUseCase {
        return SaveNotificationUseCase(repository)
    }
} 