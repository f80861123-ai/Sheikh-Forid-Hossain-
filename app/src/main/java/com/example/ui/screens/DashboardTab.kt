package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.ui.components.ChannelCard
import com.example.ui.components.FeaturedChannelCard
import com.example.ui.viewmodel.ChannelViewModel
import com.example.ui.viewmodel.SyncState
import com.example.ui.theme.CardDarkBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTab(
    viewModel: ChannelViewModel,
    onChannelSelect: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredChannels by viewModel.filteredChannels.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    val featuredChannels by viewModel.featuredChannels.collectAsState()
    val trendingChannels by viewModel.trendingChannels.collectAsState()
    val mostWatchedChannels by viewModel.mostWatchedChannels.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(syncStatus) {
        when (val state = syncStatus) {
            is SyncState.Success -> {
                Toast.makeText(context, "চ্যানেল তালিকা সফলভাবে সিঙ্ক হয়েছে!", Toast.LENGTH_SHORT).show()
            }
            is SyncState.Error -> {
                Toast.makeText(context, "ত্রুটি: ${state.message}", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    if (syncStatus is SyncState.Loading) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
            dismissButton = {},
            containerColor = CardDarkBg,
            shape = RoundedCornerShape(16.dp),
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    CircularProgressIndicator(color = GreenPrimary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "চ্যানেল লোড হচ্ছে...",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Text(
                    text = "IPTV-org ডাটাবেস থেকে সংশ্লিষ্ট দেশের সর্বশেষ .m3u প্লেলিস্ট ডাউনলোড করা হচ্ছে। অনুগ্রহ করে একটু অপেক্ষা করুন...",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        )
    }

    val countries = listOf(
        CountryData("Bangladesh", "BD", "🇧🇩", "বাংলাদেশ (Bangladesh)"),
        CountryData("India", "IN", "🇮🇳", "ভারত (India)"),
        CountryData("USA", "US", "🇺🇸", "ইউএসএ (USA)"),
        CountryData("UK", "GB", "🇬🇧", "যুক্তরাজ্য (UK)"),
        CountryData("Pakistan", "PK", "🇵🇰", "পাকিস্তান (Pakistan)"),
        CountryData("Saudi Arabia", "SA", "🇸🇦", "সৌদি আরব (Saudi Arabia)"),
        CountryData("UAE", "AE", "🇦🇪", "সংযুক্ত আরব আমিরাত (UAE)"),
        CountryData("Canada", "CA", "🇨🇦", "কানাডা (Canada)"),
        CountryData("Australia", "AU", "🇦🇺", "অস্ট্রেলিয়া (Australia)")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // --- IPTV ONLINE SYNCRONIZER ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "লাইভ স্ট্রিম সিঙ্ক (IPTV-ORG)",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "অনলাইন ডাটাবেস থেকে সর্বশেষ $selectedCountry চ্যানেল ডাউনলোড করুন",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            val activeCode = countries.find { it.name == selectedCountry }?.code ?: "BD"
                            viewModel.triggerIptvOrgSync(selectedCountry, activeCode)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = GreenPrimary.copy(alpha = 0.2f),
                            contentColor = GreenPrimary
                        ),
                        modifier = Modifier.testTag("sync_iptv_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cached,
                            contentDescription = "Sync channels"
                        )
                    }
                }
            }
        }

        // --- SECTION COUNTRY dropdown SELECTOR ---
        item {
            Column {
                Text(
                    text = "দেশ নির্বাচন করুন (Country)",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                var dropdownExpanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    val currentCountryData = countries.find { it.name == selectedCountry } ?: countries[0]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dropdownExpanded = true }
                            .testTag("country_dropdown_trigger"),
                        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GlassBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentCountryData.flag,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column {
                                    Text(
                                        text = "নির্বাচিত দেশ (Selected Country)",
                                        color = Color.LightGray,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentCountryData.banglaName,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (dropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Indicator",
                                tint = GreenPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDarkBg)
                            .border(BorderStroke(1.dp, GlassBorder), shape = RoundedCornerShape(8.dp))
                            .testTag("country_dropdown_menu")
                    ) {
                        countries.forEach { country ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(text = country.flag, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = country.banglaName,
                                            color = if (country.name == selectedCountry) GreenPrimary else Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = if (country.name == selectedCountry) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                },
                                onClick = {
                                    dropdownExpanded = false
                                    viewModel.selectCountry(country.name)
                                    // Trigger auto-fetch associated country's .m3u file
                                    viewModel.triggerIptvOrgSync(country.name, country.code)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // --- HORIZONTAL FEATURED CHANNELS ---
        val countryFeatured = featuredChannels.filter { it.country == selectedCountry }
        if (countryFeatured.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = "Featured",
                            tint = GreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ফিচার্ড স্পোর্টস চ্যানেল",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("featured_channels_row")
                    ) {
                        items(countryFeatured) { channel ->
                            FeaturedChannelCard(
                                channel = channel,
                                onChannelSelect = onChannelSelect,
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- TRENDING CHANNELS SECTION ---
        val countryTrending = trendingChannels.filter { it.country == selectedCountry }
        if (countryTrending.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Trending",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ট্রেন্ডিং চ্যানেলসমূহ",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(countryTrending) { channel ->
                            FeaturedChannelCard(
                                channel = channel,
                                onChannelSelect = onChannelSelect,
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- CATEGORIES SELECTION PILLS ---
        item {
            Column {
                Text(
                    text = "ক্যাটাগরি ফিল্টার",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setCategory(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = Color.White,
                                containerColor = CardDarkBg,
                                labelColor = Color.LightGray
                            )
                        )
                    }
                }
            }
        }

        // --- MAIN CHANNEL CHIPS LIST ---
        item {
            Text(
                text = "$selectedCountry - সব চ্যানেল (${filteredChannels.size})",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (filteredChannels.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "কোনো চ্যানেল পাওয়া যায়নি। সিঙ্ক বাটন চাপুন।",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(filteredChannels, key = { it.id }) { channel ->
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

data class CountryData(val name: String, val code: String, val flag: String, val banglaName: String)
