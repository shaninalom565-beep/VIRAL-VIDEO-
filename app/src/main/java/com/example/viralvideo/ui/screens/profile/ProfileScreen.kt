package com.example.viralvideo.ui.screens.profile

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
fun ProfileScreen(
    uiState: ProfileUiState,
    onBackClick: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onCloseEditProfile: () -> Unit,
    onNameChanged: (String) -> Unit,
    onBioChanged: (String) -> Unit,
    onSaveProfile: () -> Unit,
    onToggle2FA: () -> Unit,
    onClearHistory: () -> Unit,
    onNavigateToStudio: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onVideoClick: (String) -> Unit
) {
    val user = uiState.user

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account & Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenEditProfile, modifier = Modifier.testTag("edit_profile_button")) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header Profile Banner & Avatar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    // Cover Photo
                    AsyncImage(
                        model = user?.coverUrl ?: "https://picsum.photos/seed/cover_alex/800/300",
                        contentDescription = "Cover Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Avatar Overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 20.dp)
                    ) {
                        AsyncImage(
                            model = user?.avatarUrl ?: "https://picsum.photos/seed/user_alex/200/200",
                            contentDescription = user?.name,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .border(3.dp, ViralRed, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Verified Creator Badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(VerifiedBadgeBlue)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("VERIFIED CREATOR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }

            // User Info & Bio
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = user?.name ?: "Alex Vance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Verified", tint = VerifiedBadgeBlue, modifier = Modifier.size(18.dp))
                    }
                    Text(text = user?.handle ?: "@alexvance_viral", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = user?.bio ?: "Digital Creator | Tech & Gaming Reviews 🚀", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Account Fast Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GlassCard(modifier = Modifier.weight(1f)) {
                            Text("Subscribers", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${user?.subscribersCount ?: 680}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        GlassCard(modifier = Modifier.weight(1f)) {
                            Text("Total Views", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${formatCount(user?.totalViewsCount ?: 58920)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quick Studio & Admin Shortcuts
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Button(
                        onClick = onNavigateToStudio,
                        colors = ButtonDefaults.buttonColors(containerColor = ViralRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("open_studio_button")
                    ) {
                        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = GoldStar)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Creator Studio & Revenue Dashboard", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onNavigateToAdmin,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("open_admin_button")
                    ) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = AccentBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Admin Command Center", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Platform Settings
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "App Preferences & Security", icon = Icons.Default.Security)

                GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    // Dark Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.DarkMode, contentDescription = null, tint = ViralRed)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Dark Theme", fontWeight = FontWeight.Bold)
                        }
                        Switch(
                            checked = uiState.isDarkMode,
                            onCheckedChange = { onToggleDarkMode() }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // 2FA Security
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = AccentBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Two-Factor Authentication (2FA)", fontWeight = FontWeight.Bold)
                        }
                        Switch(
                            checked = user?.is2FAEnabled == true,
                            onCheckedChange = { onToggle2FA() }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    // Google OAuth Connection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = GoldStar)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Google OAuth Linked", fontWeight = FontWeight.Bold)
                        }
                        Text("Connected", color = VerifiedBadgeBlue, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Saved Watch Later Playlist
            if (uiState.watchLaterList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(title = "Watch Later Playlist (${uiState.watchLaterList.size})", icon = Icons.Default.Bookmark)
                }

                items(uiState.watchLaterList) { item ->
                    GlassCard(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { onVideoClick(item.videoId) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = item.thumbnailUrl,
                                contentDescription = item.title,
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(text = item.creatorName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Watch History
            if (uiState.watchHistory.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = "Watch History",
                        subtitle = "Recently played content",
                        icon = Icons.Default.History,
                        onSeeAllClick = onClearHistory
                    )
                }

                items(uiState.watchHistory.take(4)) { hist ->
                    GlassCard(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { onVideoClick(hist.videoId) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = hist.thumbnailUrl,
                                contentDescription = hist.title,
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = hist.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(text = hist.creatorName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Modal Dialog
    if (uiState.isEditProfileOpen) {
        AlertDialog(
            onDismissRequest = onCloseEditProfile,
            title = { Text("Edit Profile Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.editNameInput,
                        onValueChange = onNameChanged,
                        label = { Text("Channel / Name") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                    )
                    OutlinedTextField(
                        value = uiState.editBioInput,
                        onValueChange = onBioChanged,
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_bio_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onSaveProfile,
                    colors = ButtonDefaults.buttonColors(containerColor = ViralRed),
                    modifier = Modifier.testTag("save_profile_button")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseEditProfile) { Text("Cancel") }
            }
        )
    }
}
