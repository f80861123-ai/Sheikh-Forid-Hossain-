package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Channel
import com.example.ui.components.VideoPlayer
import com.example.ui.viewmodel.ChannelViewModel
import com.example.ui.theme.CardDarkBg
import com.example.ui.theme.DeepDarkBg
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.RedAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ChannelViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Bottom navigation current tab index
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Observe state flows
    val activeChannel by viewModel.activeChannel.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val onlineViewerCounter by viewModel.onlineViewerCounter.collectAsStateWithLifecycle()
    val stats by viewModel.appStats.collectAsStateWithLifecycle()

    // Determine current section title
    val sectionTitle = when (selectedTab) {
        0 -> "লাইভ খেলা দেখুন"
        1 -> "প্রিয় চ্যানেলসমূহ"
        2 -> "ইতিহাস (History)"
        else -> "আরো অপশন ও সাহায্য"
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_screen_scaffold"),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "খেলা দেখুন",
                                color = GreenPrimary,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(RedAccent)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LIVE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    actions = {
                        // Live dynamic counters display to reflect 100k+ users activity as requested
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33FFFFFF))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${onlineViewerCounter} লাইভ",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DeepDarkBg
                    )
                )

                // Sub-stats banner showing aggregate traffic counts
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDarkBg)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Total users",
                            tint = Color.LightGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "মোট পরিদর্শক: ${stats?.totalVisits ?: 1240} জন",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NetworkCheck,
                            contentDescription = "CDN Connection speed",
                            tint = GreenPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "সার্ভার CDN রেডি",
                            color = GreenPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardDarkBg,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("app_bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.SportsSoccer, contentDescription = "Home") },
                    label = { Text("হোম") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = GreenPrimary,
                        indicatorColor = GreenPrimary,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("প্রিয়") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = RedAccent,
                        indicatorColor = RedAccent,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.History, contentDescription = "Recents") },
                    label = { Text("ইতিহাস") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = GreenPrimary,
                        indicatorColor = GreenPrimary,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More options") },
                    label = { Text("আরো") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = GreenPrimary,
                        indicatorColor = GreenPrimary,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DeepDarkBg)
        ) {
            // Main selected section
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Inline Search Bar (Visible on Home dashboard specifically)
                if (selectedTab == 0) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("চ্যানেল খুঁজুন (যেমন BTV, Sports)", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("channel_search_bar"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedContainerColor = CardDarkBg,
                            unfocusedContainerColor = CardDarkBg,
                            cursorColor = GreenPrimary
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Render matching screen
                when (selectedTab) {
                    0 -> DashboardTab(
                        viewModel = viewModel,
                        onChannelSelect = { viewModel.setActiveChannel(it) }
                    )
                    1 -> FavoritesTab(
                        viewModel = viewModel,
                        onChannelSelect = { viewModel.setActiveChannel(it) }
                    )
                    2 -> RecentsTab(
                        viewModel = viewModel,
                        onChannelSelect = { viewModel.setActiveChannel(it) }
                    )
                    else -> MoreTab()
                }
            }

            // --- FLOATING ACTIVE MINI PLAYER OVERLAY ---
            AnimatedVisibility(
                visible = activeChannel != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp, start = 12.dp, end = 12.dp)
            ) {
                activeChannel?.let { channel ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                    ) {
                        // Embedded Actual Video Surface Player View (Auto-Reconnect and Full Actions integrated)
                        VideoPlayer(
                            channel = channel,
                            onClose = { viewModel.setActiveChannel(null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
