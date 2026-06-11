package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppStats
import com.example.data.model.Channel
import com.example.data.repository.ChannelRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChannelViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = ChannelRepository(database.channelDao(), application)

    // State parameters for lists and filters
    private val _selectedCountry = MutableStateFlow("Bangladesh")
    val selectedCountry: StateFlow<String> = _selectedCountry.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Loading status of API fetches
    private val _syncStatus = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncStatus: StateFlow<SyncState> = _syncStatus.asStateFlow()

    // Currently playing channel
    private val _activeChannel = MutableStateFlow<Channel?>(null)
    val activeChannel: StateFlow<Channel?> = _activeChannel.asStateFlow()

    // Global simulation counters
    private val _onlineViewerCounter = MutableStateFlow(106500)
    val onlineViewerCounter: StateFlow<Int> = _onlineViewerCounter.asStateFlow()

    val appStats: StateFlow<AppStats?> = repository.appStats.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppStats()
    )

    // Reactive lists coming directly from Room DB
    val favorites: StateFlow<List<Channel>> = repository.favorites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentlyWatched: StateFlow<List<Channel>> = repository.recentlyWatched.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val featuredChannels: StateFlow<List<Channel>> = repository.featuredChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val trendingChannels: StateFlow<List<Channel>> = repository.trendingChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val mostWatchedChannels: StateFlow<List<Channel>> = repository.mostWatchedChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allChannels: StateFlow<List<Channel>> = repository.allChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Combined filtered channel lists based on country, search context, and category
    val filteredChannels: StateFlow<List<Channel>> = combine(
        repository.allChannels,
        _selectedCountry,
        _searchQuery,
        _selectedCategory
    ) { channels, country, query, category ->
        channels.filter { channel ->
            val countryMatches = if (country == "All") true else channel.country == country
            val categoryMatches = if (category == "All") true else channel.category == category
            val queryMatches = if (query.isEmpty()) {
                true
            } else {
                channel.name.contains(query, ignoreCase = true) ||
                        channel.category.contains(query, ignoreCase = true)
            }
            countryMatches && categoryMatches && queryMatches
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All distinct categories for the currently active country filters
    val categories: StateFlow<List<String>> = combine(
        repository.allChannels,
        _selectedCountry
    ) { channels, country ->
        val filtered = channels.filter { if (country == "All") true else it.country == country }
        listOf("All") + filtered.map { it.category }.distinct().sorted()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = listOf("All")
    )

    init {
        viewModelScope.launch {
            // Auto check and seed on load
            repository.checkAndPreseed()

            // Run active counter ticker to simulate real-time live visitors (100k+ global users)
            launch {
                while (true) {
                    _onlineViewerCounter.value = repository.getGlobalOnlineViewers()
                    delay(4000)
                }
            }
        }
    }

    fun selectCountry(country: String) {
        _selectedCountry.value = country
        _selectedCategory.value = "All" // Reset category selection on country change
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveChannel(channel: Channel?) {
        _activeChannel.value = channel
        if (channel != null) {
            viewModelScope.launch {
                repository.recordWatch(channel.id)
            }
        }
    }

    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            repository.toggleFavorite(channel.id, !channel.isFavorite)
        }
    }

    fun triggerIptvOrgSync(country: String, code: String) {
        viewModelScope.launch {
            _syncStatus.value = SyncState.Loading("Syncing online streams...")
            val result = repository.fetchChannelsFromIptvOrg(country, code)
            result.onSuccess { count ->
                _syncStatus.value = SyncState.Success("Synced $count streams successfully!")
                // Keep success message visible for 2 seconds
                delay(2000)
                _syncStatus.value = SyncState.Idle
            }.onFailure { error ->
                _syncStatus.value = SyncState.Error("Sync failed: ${error.localizedMessage ?: "Network error"}")
                delay(3000)
                _syncStatus.value = SyncState.Idle
            }
        }
    }
}

sealed interface SyncState {
    object Idle : SyncState
    data class Loading(val message: String) : SyncState
    data class Success(val message: String) : SyncState
    data class Error(val message: String) : SyncState
}
