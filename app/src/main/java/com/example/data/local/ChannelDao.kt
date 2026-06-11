package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppStats
import com.example.data.model.Channel
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels ORDER BY name ASC")
    fun getAllChannels(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE country = :country ORDER BY name ASC")
    fun getChannelsByCountry(country: String): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE lastWatchedTime IS NOT NULL ORDER BY lastWatchedTime DESC LIMIT 20")
    fun getRecentlyWatched(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE isFeatured = 1")
    fun getFeaturedChannels(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE isTrending = 1")
    fun getTrendingChannels(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE isMostWatched = 1")
    fun getMostWatchedChannels(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE id = :channelId LIMIT 1")
    suspend fun getChannelById(channelId: String): Channel?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(channels: List<Channel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(channel: Channel)

    @Update
    suspend fun updateChannel(channel: Channel)

    @Query("UPDATE channels SET isFavorite = :isFavorite WHERE id = :channelId")
    suspend fun updateFavorite(channelId: String, isFavorite: Boolean)

    @Query("UPDATE channels SET lastWatchedTime = :timestamp, simulatedViewerCount = :viewerCount WHERE id = :channelId")
    suspend fun updateLastWatched(channelId: String, timestamp: Long, viewerCount: Int)

    @Query("DELETE FROM channels WHERE country = :country")
    suspend fun deleteChannelsByCountry(country: String)

    // App Statistics queries
    @Query("SELECT * FROM app_stats WHERE id = 'only_stats' LIMIT 1")
    suspend fun getStats(): AppStats?

    @Query("SELECT * FROM app_stats WHERE id = 'only_stats' LIMIT 1")
    fun getStatsFlow(): Flow<AppStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: AppStats)

    @Query("UPDATE app_stats SET totalVisits = totalVisits + 1 WHERE id = 'only_stats'")
    suspend fun incrementVisits()
}
