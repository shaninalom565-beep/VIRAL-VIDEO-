package com.example.viralvideo.ui.screens.player

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.viralvideo.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    uiState: PlayerUiState,
    onBackClick: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onSetSpeed: (Float) -> Unit,
    onSetQuality: (String) -> Unit,
    onToggleSubtitles: () -> Unit,
    onToggleTheaterMode: () -> Unit,
    onTogglePiP: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleWatchLater: () -> Unit,
    onCommentInputChanged: (String) -> Unit,
    onSubmitComment: () -> Unit,
    onLikeComment: (String) -> Unit,
    onRelatedVideoClick: (String) -> Unit
) {
    var showQualityDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }

    val video = uiState.currentVideo

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = video?.title ?: "VIRAL Player",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showQualityDialog = true }) {
                        Text(
                            text = uiState.quality.take(5),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ViralRed
                        )
                    }
                    IconButton(onClick = onToggleTheaterMode) {
                        Icon(
                            imageVector = if (uiState.isTheaterMode) Icons.Default.AspectRatio else Icons.Default.CropFree,
                            contentDescription = "Theater Mode",
                            tint = if (uiState.isTheaterMode) ViralRed else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (video == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ViralRed)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Player Surface Canvas
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (uiState.isTheaterMode) 280.dp else 220.dp)
                        .background(Color.Black)
                ) {
                    // Simulated Video Thumbnail Background
                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Subtitles Overlay if Enabled
                    if (uiState.isSubtitlesOn) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 60.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.8f))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "CC: [AI Speech] Breaking video technology news...",
                                color = GoldStar,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Player Overlay Controls Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Center Play/Pause Button
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(ViralRed.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play Pause",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Bottom Seekbar & Controls Bar
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Slider(
                            value = uiState.currentProgressSec.toFloat(),
                            onValueChange = { onSeekTo(it.toInt()) },
                            valueRange = 0f..865f,
                            colors = SliderDefaults.colors(
                                thumbColor = ViralRed,
                                activeTrackColor = ViralRed,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.height(24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatTime(uiState.currentProgressSec),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " / 14:25",
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Subtitles
                                Icon(
                                    imageVector = Icons.Default.ClosedCaption,
                                    contentDescription = "CC",
                                    tint = if (uiState.isSubtitlesOn) ViralRed else Color.White,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onToggleSubtitles() }
                                )

                                // Speed
                                Text(
                                    text = "${uiState.playbackSpeed}x",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { showSpeedDialog = true }
                                )

                                // Fullscreen / PiP
                                Icon(
                                    imageVector = Icons.Default.PictureInPicture,
                                    contentDescription = "PiP",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable { onTogglePiP() }
                                )
                            }
                        }
                    }
                }
            }

            // Video Title & Stats
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${formatCount(video.viewsCount)} views",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = video.uploadDate,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ViralRed.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = video.category,
                                color = ViralRed,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons Row (Like, Dislike, Share, Save)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Row(
                                    modifier = Modifier
                                        .clickable { onToggleLike() }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                                        contentDescription = "Like",
                                        tint = if (uiState.isLiked) ViralRed else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = formatCount(video.likesCount + if (uiState.isLiked) 1 else 0),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Divider(
                                    modifier = Modifier
                                        .height(18.dp)
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Icon(
                                    imageVector = Icons.Default.ThumbDownOffAlt,
                                    contentDescription = "Dislike",
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .size(18.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Share",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (uiState.isSavedWatchLater) ViralRed.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { onToggleWatchLater() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (uiState.isSavedWatchLater) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Save",
                                    tint = if (uiState.isSavedWatchLater) ViralRed else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (uiState.isSavedWatchLater) "Saved" else "Watch Later",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.isSavedWatchLater) ViralRed else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Creator Channel Bar
                    GlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AsyncImage(
                                    model = video.creatorAvatar,
                                    contentDescription = video.creatorName,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, ViralRed, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = video.creatorName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified",
                                            tint = VerifiedBadgeBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = "1.25M subscribers",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = ViralRed),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Subscribe",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // In-Player Advertisement (Pre-roll / SocialBar)
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.horizontalGradient(listOf(DarkCard, Color(0xFF1E1020)))),
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldStar)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AD",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cyber VPN Pro - Fast Video Streaming",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Unblock ultra 4K speed globally. Try 7 days free.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Text("Install", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Comments Section Header & Input
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Comments (${uiState.comments.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.commentInputText,
                            onValueChange = onCommentInputChanged,
                            placeholder = { Text("Add a public comment...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("comment_input"),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true
                        )

                        IconButton(
                            onClick = onSubmitComment,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ViralRed)
                                .testTag("send_comment_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Comments List
            items(uiState.comments) { comment ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AsyncImage(
                            model = comment.userAvatar,
                            contentDescription = comment.userName,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = comment.userName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = comment.timestamp,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (comment.isPinned) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ViralRed.copy(alpha = 0.15f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "Pinned",
                                            color = ViralRed,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = comment.text,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { onLikeComment(comment.id) }
                                    .padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Like Comment",
                                    tint = if (comment.isLiked) ViralRed else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${comment.likesCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Divider(
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }
            }

            // Related / Up Next Videos
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Up Next",
                    subtitle = "Recommended for you",
                    icon = Icons.Default.PlaylistPlay
                )
            }

            items(uiState.relatedVideos) { related ->
                VideoCard(
                    video = related,
                    onClick = { onRelatedVideoClick(related.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }

    // Quality Selection Dialog
    if (showQualityDialog) {
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text("Select Playback Quality") },
            text = {
                Column {
                    listOf("1080p 60fps HD", "720p HD", "480p SD", "Auto (720p)").forEach { q ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSetQuality(q)
                                    showQualityDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.quality == q,
                                onClick = {
                                    onSetQuality(q)
                                    showQualityDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = q, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQualityDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Speed Selection Dialog
    if (showSpeedDialog) {
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Playback Speed") },
            text = {
                Column {
                    listOf(0.5f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSetSpeed(speed)
                                    showSpeedDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.playbackSpeed == speed,
                                onClick = {
                                    onSetSpeed(speed)
                                    showSpeedDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (speed == 1.0f) "Normal (1.0x)" else "${speed}x",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpeedDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
