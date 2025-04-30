package com.example.notificationcore.di

import android.content.Context
import androidx.room.Room
import com.example.notificationcore.NotificationDatabase
import com.example.notificationcore.NotificationRepository
import com.example.notificationcore.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationDatabase(
        @ApplicationContext context: Context
    ): NotificationDatabase {
        return Room.databaseBuilder(
            context,
            NotificationDatabase::class.java,
            "notification_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        database: NotificationDatabase
    ): NotificationRepository {
        return NotificationRepositoryImpl(database.notificationDao())
    }

    @Binds
    abstract fun bindNotificationRepository(
        repository: NotificationRepositoryImpl
    ): NotificationRepository
} 