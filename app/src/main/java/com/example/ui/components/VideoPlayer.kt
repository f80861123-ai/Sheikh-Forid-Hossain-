package com.example.ui.components

import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Log
import android.util.Rational
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.model.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
    channel: Channel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var autoReconnectAttempts by remember { mutableIntStateOf(0) }
    var showControllerOverlay by remember { mutableStateOf(true) }

    // ExoPlayer management
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = true
            }
    }

    // App Lifecycle State Observer to pause video play in background
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_PAUSE) {
                exoPlayer.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Set source HLS url
    LaunchedEffect(channel.streamUrl) {
        isLoading = true
        errorMessage = null
        autoReconnectAttempts = 0
        try {
            val mediaItem = MediaItem.Builder()
                .setUri(channel.streamUrl)
                .setMimeType(MimeTypes.APPLICATION_M3U8) // HLS parsing hint
                .build()
            
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        } catch (e: Exception) {
            errorMessage = "Initialization error: ${e.message}"
            isLoading = false
        }
    }

    // Auto Controller fade-out scheduler
    LaunchedEffect(showControllerOverlay, isPlaying) {
        if (showControllerOverlay && isPlaying) {
            delay(5000)
            showControllerOverlay = false
        }
    }

    // Listener for state checks and auto reconnection!
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                isLoading = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) {
                    errorMessage = null
                    autoReconnectAttempts = 0 // Reset attempt counter on successful loads
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isLoading = false
                Log.e("VideoPlayer", "ExoPlayer Error: ${error.message} (Code: ${error.errorCode})")
                
                if (autoReconnectAttempts < 5) {
                    val nextAttempt = autoReconnectAttempts + 1
                    autoReconnectAttempts = nextAttempt
                    errorMessage = "সংযোগ বিচ্ছিন্ন হয়েছে। পুনরায় চেষ্টা করা হচ্ছে... (চেষ্টা $nextAttempt/5)"
                    
                    scope.launch {
                        delay(4000) // 4s retry backoff
                        errorMessage = "পুনরায় সংযোগ করা হচ্ছে..."
                        isLoading = true
                        exoPlayer.prepare()
                        exoPlayer.play()
                    }
                } else {
                    errorMessage = "স্ট্রীমটি লোড করা সম্ভব হচ্ছে না। অনুগ্রহ করে অন্য চ্যানেল চেষ্টা করুন।"
                }
            }

            override fun onIsPlayingChanged(isPlayingChanged: Boolean) {
                isPlaying = isPlayingChanged
            }
        }

        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Main layout container holding video surface + layers of controls
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
            .clickable { showControllerOverlay = !showControllerOverlay }
            .testTag("full_video_player")
    ) {
        // Video Surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // Use our custom premium Glassmorphic controls
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Loading Progress Spin
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "বাফারিং হচ্ছে...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Error notifications with a manual retry button
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC080D0F)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            errorMessage = null
                            autoReconnectAttempts = 0
                            isLoading = true
                            exoPlayer.prepare()
                            exoPlayer.play()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("পুনরায় চেষ্টা করুন")
                    }
                }
            }
        }

        // Custom premium control overlays fading on inactivity
        if (showControllerOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
                    .padding(12.dp)
            ) {
                // Top control status bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LIVE",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = channel.name,
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "${channel.category} • ${channel.country}",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }

                    // Action buttons (PIP / Close)
                    Row {
                        // Picture In Picture Button (PIP)
                        IconButton(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val rational = Rational(16, 9)
                                    val params = PictureInPictureParams.Builder()
                                        .setAspectRatio(rational)
                                        .build()
                                    activity?.enterPictureInPictureMode(params)
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureInPicture,
                                contentDescription = "PIP Mode",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        // Standard Close Button
                        IconButton(
                            onClick = onClose,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("close_player_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Close Stream",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Middle large play button
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fast Rewind
                    IconButton(
                        onClick = {
                            val newPosition = (exoPlayer.currentPosition - 10000).coerceAtLeast(0)
                            exoPlayer.seekTo(newPosition)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0x33FFFFFF),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Rewind 10 Seconds",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Play/Pause
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Fast Forward
                    IconButton(
                        onClick = {
                            val newPosition = (exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration)
                            exoPlayer.seekTo(newPosition)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0x33FFFFFF),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Forward 10 Seconds",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Bottom bar stream information (simulated views etc.)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Viewers",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${channel.simulatedViewerCount + autoReconnectAttempts} দেখছেন",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Simple refresh button to force manual HLS reconnect
                    IconButton(
                        onClick = {
                            errorMessage = null
                            autoReconnectAttempts = 0
                            isLoading = true
                            exoPlayer.prepare()
                            exoPlayer.play()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0x22FFFFFF)
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload stream",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
