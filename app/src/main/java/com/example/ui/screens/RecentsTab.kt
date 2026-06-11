package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Channel
import com.example.ui.components.ChannelCard
import com.example.ui.viewmodel.ChannelViewModel
import com.example.ui.theme.GreenPrimary

@Composable
fun RecentsTab(
    viewModel: ChannelViewModel,
    onChannelSelect: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    val recentlyWatched by viewModel.recentlyWatched.collectAsState()

    if (recentlyWatched.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "No History",
                    tint = GreenPrimary.copy(alpha = 0.45f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "সম্প্রতি কোনো চ্যানেল চালানো হয়নি",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "যেকোনো স্ট্রিম প্লে করলে তা স্বয়ংক্রিয়ভাবে এখানে তালিকাভুক্ত হবে",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("recents_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
        ) {
            item {
                Text(
                    text = "সম্প্রতি দেখা চ্যানেলসমূহ (${recentlyWatched.size})",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(recentlyWatched, key = { it.id }) { channel ->
                ChannelCard(
                    channel = channel,
                    onChannelSelect = onChannelSelect,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
