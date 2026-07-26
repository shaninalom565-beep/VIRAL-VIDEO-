package com.example.viralvideo.ui.screens.admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viralvideo.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    uiState: AdminPanelUiState,
    onBackClick: () -> Unit,
    onToggleAdStatus: (com.example.viralvideo.data.local.AdConfigEntity) -> Unit,
    onApproveWithdraw: (String) -> Unit,
    onRejectWithdraw: (String) -> Unit,
    onOpenNewAdDialog: () -> Unit,
    onCloseNewAdDialog: () -> Unit,
    onNewAdTitleChanged: (String) -> Unit,
    onNewAdFormatChanged: (String) -> Unit,
    onNewAdPositionChanged: (String) -> Unit,
    onSubmitCreateAd: () -> Unit,
    onDeleteVideo: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = ViralRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Admin Command Center", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenNewAdDialog, modifier = Modifier.testTag("admin_add_ad_button")) {
                        Icon(imageVector = Icons.Default.AddBox, contentDescription = "New Ad", tint = GoldStar)
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
            // Analytics Stats Overview
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Platform Real-time Health", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Total Users",
                            value = "${formatCount(uiState.totalUsers.toLong())}",
                            subtitle = "+2.4K today",
                            icon = Icons.Default.People,
                            iconColor = AccentBlue,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Creators",
                            value = "${formatCount(uiState.totalCreators.toLong())}",
                            subtitle = "+120 today",
                            icon = Icons.Default.Videocam,
                            iconColor = VerifiedBadgeBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            title = "Videos Hosted",
                            value = "${uiState.videos.size}",
                            subtitle = "Streaming live",
                            icon = Icons.Default.PlayCircle,
                            iconColor = ViralRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Platform Gross",
                            value = "$89.4K",
                            subtitle = "30% share",
                            icon = Icons.Default.AttachMoney,
                            iconColor = GoldStar,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Advertisements Manager Section
            item {
                SectionHeader(
                    title = "Advertisement Engine Management",
                    subtitle = "Control Adsterra, SocialBar & Popunder ads",
                    icon = Icons.Default.Campaign,
                    onSeeAllClick = onOpenNewAdDialog
                )
            }

            items(uiState.ads) { ad ->
                GlassCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(GoldStar)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = ad.format, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = ad.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Position: ${ad.position} • Impr: ${formatCount(ad.impressions.toLong())} • Clicks: ${ad.clicks}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = ad.isActive,
                            onCheckedChange = { onToggleAdStatus(ad) },
                            colors = SwitchDefaults.colors(checkedThumbColor = ViralRed, checkedTrackColor = ViralRed.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            // Pending Withdraw Approval Queue Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Creator Payout Request Queue",
                    subtitle = "Approve or reject creator withdrawals",
                    icon = Icons.Default.Payment
                )
            }

            items(uiState.withdrawRequests) { req ->
                GlassCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$${String.format("%.2f", req.amount)} -> ${req.accountDetails}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Method: ${req.method} • Status: ${req.status}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (req.status == "Pending") {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { onApproveWithdraw(req.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Approve", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { onRejectWithdraw(req.id) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Reject", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (req.status == "Approved") AccentBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = req.status,
                                    color = if (req.status == "Approved") AccentBlue else MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Platform Content Moderation Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Content Moderation & System Videos",
                    subtitle = "Review public platform streams",
                    icon = Icons.Default.Shield
                )
            }

            items(uiState.videos) { video ->
                GlassCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = video.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(text = "Creator: ${video.creatorName} • Category: ${video.category}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        IconButton(onClick = { onDeleteVideo(video.id) }) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    // Create New Ad Campaign Dialog
    if (uiState.isNewAdDialogOpen) {
        AlertDialog(
            onDismissRequest = onCloseNewAdDialog,
            title = { Text("Create New Ad Placement") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.newAdTitle,
                        onValueChange = onNewAdTitleChanged,
                        label = { Text("Campaign Name / Title") },
                        modifier = Modifier.fillMaxWidth().testTag("ad_title_input")
                    )
                    OutlinedTextField(
                        value = uiState.newAdFormat,
                        onValueChange = onNewAdFormatChanged,
                        label = { Text("Ad Format (Native, SocialBar, Pre-roll)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.newAdPosition,
                        onValueChange = onNewAdPositionChanged,
                        label = { Text("Position (Homepage, Player, Sidebar)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onSubmitCreateAd,
                    colors = ButtonDefaults.buttonColors(containerColor = ViralRed),
                    modifier = Modifier.testTag("submit_ad_button")
                ) {
                    Text("Launch Ad Placement")
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseNewAdDialog) { Text("Cancel") }
            }
        )
    }
}
