package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey val id: String, // Stream URL or sanitized channel ID
    val name: String,
    val logoUrl: String?,
    val streamUrl: String,
    val country: String,      // Country name (e.g. Bangladesh, India, USA)
    val category: String,     // Category name (e.g. Sports, Entertainment, News)
    val isFavorite: Boolean = false,
    val lastWatchedTime: Long? = null,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isMostWatched: Boolean = false,
    val simulatedViewerCount: Int = 0
) : Serializable
