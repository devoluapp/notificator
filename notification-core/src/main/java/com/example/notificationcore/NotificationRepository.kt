package com.example.notificationcore

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun insertNotification(notification: Notification)
    suspend fun getNotificationById(id: String): Notification?
    fun getAllNotifications(): Flow<List<Notification>>
    suspend fun markAsRead(id: String)
    suspend fun deleteNotification(id: String)
} 