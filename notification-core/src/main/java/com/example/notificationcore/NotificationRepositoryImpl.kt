package com.example.notificationcore

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean
)

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getById(id: Int): NotificationEntity?

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    suspend fun getAll(): List<NotificationEntity>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Int)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun delete(id: Int)
}

@Database(entities = [NotificationEntity::class], version = 1)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {
    
    override suspend fun insertNotification(notification: Notification) {
        notificationDao.insert(notification)
    }

    override suspend fun getNotificationById(id: String): Notification? {
        return notificationDao.getById(id)
    }

    override fun getAllNotifications(): Flow<List<Notification>> {
        return notificationDao.getAll()
    }

    override suspend fun markAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    override suspend fun deleteNotification(id: String) {
        notificationDao.delete(id)
    }
} 