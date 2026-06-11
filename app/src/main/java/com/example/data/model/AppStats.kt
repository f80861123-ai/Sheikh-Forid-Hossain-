package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_stats")
data class AppStats(
    @PrimaryKey val id: String = "only_stats",
    val totalVisits: Int = 0,
    val showBannerNotifications: Boolean = true
)
