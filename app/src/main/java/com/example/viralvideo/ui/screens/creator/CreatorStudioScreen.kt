package com.example.viralvideo.ui.screens.creator

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
fun CreatorStudioScreen(
    uiState: CreatorStudioUiState,
    onBackClick: () -> Unit,
    onOpenUploadDialog: () -> Unit,
    onCloseUploadDialog: () -> Unit,
    onOpenWithdrawDialog: () -> Unit,
    onCloseWithdrawDialog: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onCategoryChanged: (String) -> Unit,
    onTagsChanged: (String) -> Unit,
    onWithdrawAmountChanged: (String) -> Unit,
    onWithdrawMethodChanged: (String) -> Unit,
    onWithdrawAccountChanged: (String) -> Unit,
    onSubmitUpload: () -> Unit,
    onSubmitWithdraw: () -> Unit,
    onDeleteVideo: (String) -> Unit
) {
    val user = uiState.user

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Creator Studio & Monetization",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenUploadDialog, modifier = Modifier.testTag("studio_upload_button")) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload", tint = ViralRed)
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
            // Monetization Header Banner
            item {
                GlassCard(
                    modifier = Modifier.padding(16.dp),
                    backgroundColor = DarkCard
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Revenue Split: 70% Creator / 30% Platform",
                                    color = GoldStar,
                                    fontWeight = FontWeight.Black,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your Monetization Overview",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = onOpenWithdrawDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = ViralRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Withdraw", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Cards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Total Revenue",
                            value = "$${String.format("%.2f", user?.totalEarnings ?: 540.80)}",
                            subtitle = "+18% this month",
                            icon = Icons.Default.AttachMoney,
                            iconColor = GoldStar,
                            modifier = Modifier.weight(1f)
                        )

                        StatCard(
                            title = "Pending Payout",
                            value = "$${String.format("%.2f", user?.pendingWithdraw ?: 150.00)}",
                            subtitle = "Available now",
                            icon = Icons.Default.AccountBalance,
                            iconColor = AccentBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Eligibility Requirements Checklist Section
            item {
                SectionHeader(
                    title = "Monetization Eligibility",
                    subtitle = "Requirements to unlock 70% revenue share",
                    icon = Icons.Default.Verified
                )

                GlassCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                    val subs = user?.subscribersCount ?: 680
                    val views = user?.totalViewsCount ?: 58920
                    val vids = user?.publicVideosCount ?: 11
                    val age = user?.accountAgeDays ?: 42

                    EligibilityCheckItem(
                        title = "500 Subscribers",
                        current = "$subs / 500",
                        isMet = subs >= 500,
                        progress = (subs / 500f).coerceIn(0f, 1f)
                    )
                    EligibilityCheckItem(
                        title = "50,000 Total Views",
                        current = "${formatCount(views)} / 50K",
                        isMet = views >= 50000,
                        progress = (views / 50000f).coerceIn(0f, 1f)
                    )
                    EligibilityCheckItem(
                        title = "10 Public Videos",
                        current = "$vids / 10",
                        isMet = vids >= 10,
                        progress = (vids / 10f).coerceIn(0f, 1f)
                    )
                    EligibilityCheckItem(
                        title = "Account Age 30 Days",
                        current = "$age days / 30",
                        isMet = age >= 30,
                        progress = (age / 30f).coerceIn(0f, 1f)
                    )
                    EligibilityCheckItem(
                        title = "Verified Identity & No Strikes",
                        current = if (user?.isIdentityVerified == true) "Verified" else "Pending",
                        isMet = user?.isIdentityVerified == true,
                        progress = 1f
                    )
                }
            }

            // My Videos List Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Manage Uploaded Content (${uiState.myVideos.size})",
                    subtitle = "Track views & earnings per video",
                    icon = Icons.Default.VideoLibrary
                )
            }

            // My Videos Items
            items(uiState.myVideos) { video ->
                GlassCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(110.dp)
                                .height(68.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black)
                        ) {
                            AsyncImage(
                                model = video.thumbnailUrl,
                                contentDescription = video.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = video.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "${formatCount(video.viewsCount)} views • ${video.uploadDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Est. Earned: $${String.format("%.2f", (video.viewsCount * 0.0025))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldStar,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = { onDeleteVideo(video.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Video",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Payout / Withdraw History Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "Withdraw History",
                    subtitle = "Past payouts & transaction logs",
                    icon = Icons.Default.History
                )
            }

            items(uiState.withdrawHistory) { historyItem ->
                GlassCard(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$${String.format("%.2f", historyItem.amount)} via ${historyItem.method}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${historyItem.accountDetails} • ${historyItem.requestDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (historyItem.status == "Approved") AccentBlue.copy(alpha = 0.2f)
                                    else GoldStar.copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = historyItem.status.uppercase(),
                                color = if (historyItem.status == "Approved") AccentBlue else GoldStar,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }

    // Video Upload Modal Dialog
    if (uiState.isUploadDialogOpen) {
        AlertDialog(
            onDismissRequest = onCloseUploadDialog,
            title = { Text("Upload New Video") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.newVideoTitle,
                        onValueChange = onTitleChanged,
                        label = { Text("Video Title") },
                        modifier = Modifier.fillMaxWidth().testTag("video_title_input")
                    )
                    OutlinedTextField(
                        value = uiState.newVideoCategory,
                        onValueChange = onCategoryChanged,
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.newVideoTags,
                        onValueChange = onTagsChanged,
                        label = { Text("Tags (comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onSubmitUpload,
                    colors = ButtonDefaults.buttonColors(containerColor = ViralRed),
                    modifier = Modifier.testTag("submit_video_button")
                ) {
                    Text("Publish Video")
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseUploadDialog) {
                    Text("Cancel")
                }
            }
        )
    }

    // Withdraw Payout Modal Dialog
    if (uiState.isWithdrawDialogOpen) {
        AlertDialog(
            onDismissRequest = onCloseWithdrawDialog,
            title = { Text("Request Earnings Payout") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.withdrawAmountInput,
                        onValueChange = onWithdrawAmountChanged,
                        label = { Text("Withdraw Amount ($)") },
                        modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_input")
                    )
                    OutlinedTextField(
                        value = uiState.withdrawAccountInput,
                        onValueChange = onWithdrawAccountChanged,
                        label = { Text("Payment Destination (Email or Account)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onSubmitWithdraw,
                    colors = ButtonDefaults.buttonColors(containerColor = ViralRed),
                    modifier = Modifier.testTag("submit_withdraw_button")
                ) {
                    Text("Submit Request")
                }
            },
            dismissButton = {
                TextButton(onClick = onCloseWithdrawDialog) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EligibilityCheckItem(
    title: String,
    current: String,
    isMet: Boolean,
    progress: Float
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isMet) VerifiedBadgeBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
            Text(text = current, style = MaterialTheme.typography.labelMedium, color = if (isMet) VerifiedBadgeBlue else MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = if (isMet) VerifiedBadgeBlue else ViralRed,
            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    }
}

