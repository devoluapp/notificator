package com.example.notificationcore

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
) 