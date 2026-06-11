package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.ChannelDao
import com.example.data.model.AppStats
import com.example.data.model.Channel
import com.example.data.model.PreseededChannels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class ChannelRepository(
    private val channelDao: ChannelDao,
    private val context: Context
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // Stream Flows from Room DB for modern reactive Jetpack Compose consumption
    val allChannels: Flow<List<Channel>> = channelDao.getAllChannels()
    val favorites: Flow<List<Channel>> = channelDao.getFavorites()
    val recentlyWatched: Flow<List<Channel>> = channelDao.getRecentlyWatched()
    val featuredChannels: Flow<List<Channel>> = channelDao.getFeaturedChannels()
    val trendingChannels: Flow<List<Channel>> = channelDao.getTrendingChannels()
    val mostWatchedChannels: Flow<List<Channel>> = channelDao.getMostWatchedChannels()
    val appStats: Flow<AppStats?> = channelDao.getStatsFlow()

    // Preseed initial data if DB is blank
    suspend fun checkAndPreseed() = withContext(Dispatchers.IO) {
        try {
            val stats = channelDao.getStats()
            if (stats == null) {
                channelDao.insertStats(AppStats(id = "only_stats", totalVisits = 1))
            } else {
                channelDao.incrementVisits()
            }

            val existingChannelsCount = channelDao.getAllChannels().firstOrNull()?.size ?: 0
            if (existingChannelsCount == 0) {
                Log.d("ChannelRepository", "Database is empty. Preseeding initial channels...")
                val initialList = PreseededChannels.getList()
                channelDao.insertAll(initialList)
            }
        } catch (e: Exception) {
            Log.e("ChannelRepository", "Preseed error: ${e.message}", e)
        }
    }

    fun getChannelsByCountry(country: String): Flow<List<Channel>> {
        return channelDao.getChannelsByCountry(country)
    }

    suspend fun toggleFavorite(channelId: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        channelDao.updateFavorite(channelId, isFavorite)
    }

    suspend fun recordWatch(channelId: String) = withContext(Dispatchers.IO) {
        val currentChannel = channelDao.getChannelById(channelId)
        val randomViewerAdjustment = Random.nextInt(50, 450)
        val viewers = (currentChannel?.simulatedViewerCount ?: 100) + randomViewerAdjustment
        channelDao.updateLastWatched(channelId, System.currentTimeMillis(), viewers)
    }

    // Live counter generator for simulation element (Realtime Sync style)
    suspend fun getGlobalOnlineViewers(): Int = withContext(Dispatchers.IO) {
        // Simulates the aggregate size of high IPTV traffic (e.g. 100k+ active concurrent users)
        return@withContext Random.nextInt(104000, 112500)
    }

    // Fetch and parse live channels from IPTV-org (M3U files)!
    suspend fun fetchChannelsFromIptvOrg(countryName: String, countryCode: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            // Mapping for country-specific file fetches
            val url = "https://iptv-org.github.io/iptv/countries/${countryCode.lowercase()}.m3u"
            Log.d("ChannelRepository", "Fetching live M3U lists for $countryName from $url")

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP error ${response.code}"))
            }

            val responseBody = response.body ?: return@withContext Result.failure(Exception("Empty response body"))
            val reader = BufferedReader(InputStreamReader(responseBody.byteStream()))
            
            val parsedChannels = ArrayList<Channel>()
            var line: String?
            
            var currentTvgId = ""
            var currentLogo = ""
            var currentGroup = "General"
            var currentName = ""

            while (reader.readLine().also { line = it } != null) {
                val currentLine = line!!.trim()
                if (currentLine.startsWith("#EXTINF:")) {
                    // Reset single channel state
                    currentTvgId = ""
                    currentLogo = ""
                    currentGroup = "Entertainment"
                    currentName = ""

                    // Extract logo
                    val logoRegex = """tvg-logo="([^"]+)"""".toRegex()
                    val logoMatch = logoRegex.find(currentLine)
                    if (logoMatch != null) {
                        currentLogo = logoMatch.groupValues[1]
                    }

                    // Extract group / category
                    val groupRegex = """group-title="([^"]+)"""".toRegex()
                    val groupMatch = groupRegex.find(currentLine)
                    if (groupMatch != null) {
                        currentGroup = groupMatch.groupValues[1]
                    }

                    // Extract ID
                    val idRegex = """tvg-id="([^"]+)"""".toRegex()
                    val idMatch = idRegex.find(currentLine)
                    if (idMatch != null) {
                        currentTvgId = idMatch.groupValues[1]
                    }

                    // Extract channel name (lies after the last comma)
                    val commaIndex = currentLine.lastIndexOf(',')
                    if (commaIndex != -1 && commaIndex < currentLine.length - 1) {
                        currentName = currentLine.substring(commaIndex + 1).trim()
                    }
                } else if (currentLine.isNotEmpty() && !currentLine.startsWith("#")) {
                    // This is the stream URL line!
                    if (currentName.isEmpty()) {
                        // Use filename/domain name as channel name if missing
                        currentName = currentLine.substringAfterLast("/").substringBeforeLast(".")
                    }
                    if (currentTvgId.isEmpty()) {
                        currentTvgId = currentLine.hashCode().toString()
                    }

                    // Quality check
                    if (currentLine.startsWith("http")) {
                        val isFeatured = currentGroup.equals("Sports", ignoreCase = true)
                        
                        parsedChannels.add(
                            Channel(
                                id = "${countryCode}_${currentTvgId}_${parsedChannels.size}",
                                name = currentName,
                                logoUrl = if (currentLogo.isNotEmpty()) currentLogo else null,
                                streamUrl = currentLine,
                                country = countryName,
                                category = if (currentGroup.isNotEmpty()) currentGroup else "General",
                                isFavorite = false,
                                isFeatured = isFeatured,
                                isTrending = false,
                                isMostWatched = false,
                                simulatedViewerCount = Random.nextInt(120, 1800)
                            )
                        )
                    }
                }
            }

            if (parsedChannels.isNotEmpty()) {
                // Clear and save to avoid duplicates
                channelDao.deleteChannelsByCountry(countryName)
                channelDao.insertAll(parsedChannels)
                return@withContext Result.success(parsedChannels.size)
            } else {
                return@withContext Result.failure(Exception("No streams parsed from M3U"))
            }
        } catch (e: Exception) {
            Log.e("ChannelRepository", "Failed saving IPTV-org list for $countryName", e)
            return@withContext Result.failure(e)
        }
    }
}
