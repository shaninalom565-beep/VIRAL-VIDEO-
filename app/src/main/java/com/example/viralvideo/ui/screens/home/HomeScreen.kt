package com.example.viralvideo.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.*
import com.example.viralvideo.data.local.CreatorEntity
import com.example.viralvideo.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onVideoClick: (String) -> Unit,
    onShortClick: (String) -> Unit,
    onSubscribeToggle: (CreatorEntity) -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCreatorStudio: () -> Unit
) {
    val categories = listOf("All", "Trending", "Shorts", "Technology", "Gaming", "Music", "Education")

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FrostedHeaderBg)
                    .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(0.dp))
            ) {
                // Brand Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ViralRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "VIRAL VIDEO",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "VIRAL",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "VIDEO",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = ViralRed,
                                letterSpacing = (-0.5).sp
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x1AFFFFFF))
                                .clickable { onNavigateToSearch() }
                                .testTag("search_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(ViralRed)
                                .clickable { onNavigateToUpload() }
                                .testTag("upload_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Upload",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Category Pills
                CategoryPillsRow(
                    categories = categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Banner Section
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black, spotColor = Color.White.copy(alpha = 0.1f))
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(24.dp)),
                    color = Color(0x14FFFFFF)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        // Hero Banner Image
                        Image(
                            painter = painterResource(id = R.drawable.img_hero_banner_1785094375354),
                            contentDescription = "VIRAL VIDEO Platform Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Dark Gradient Overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.85f),
                                            Color.Black.copy(alpha = 0.4f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(ViralRed)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = GoldStar,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CREATOR MONETIZATION LIVE",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                            }

                            Column {
                                Text(
                                    text = "Share Videos & Earn 70% Revenue",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Join 100,000+ creators earning weekly payouts.",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            Button(
                                onClick = onNavigateToCreatorStudio,
                                colors = ButtonDefaults.buttonColors(containerColor = ViralRed),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Open Creator Studio",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Advertisement Placement (Adsterra / SocialBar)
            if (uiState.ads.isNotEmpty()) {
                item {
                    AdBannerView(
                        ad = uiState.ads.first(),
                        onAdClick = { }
                    )
                }
            }

            // Shorts Reel Section
            if (uiState.shorts.isNotEmpty() && (uiState.selectedCategory == "All" || uiState.selectedCategory == "Shorts")) {
                item {
                    SectionHeader(
                        title = "VIRAL Shorts",
                        subtitle = "Trending 60s vertical clips",
                        icon = Icons.Default.FlashOn
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.shorts) { shortVideo ->
                            ShortCard(
                                video = shortVideo,
                                onClick = { onShortClick(shortVideo.id) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Featured Creators Section
            if (uiState.creators.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Top Verified Creators",
                        subtitle = "Popular viral channels",
                        icon = Icons.Default.Stars
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.creators) { creator ->
                            Surface(
                                modifier = Modifier
                                    .width(140.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(
                                        1.dp,
                                        Color(0x1AFFFFFF),
                                        RoundedCornerShape(20.dp)
                                    ),
                                color = Color(0x14FFFFFF)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AsyncImage(
                                        model = creator.avatarUrl,
                                        contentDescription = creator.name,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, ViralRed, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = creator.name,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${formatCount(creator.subscribersCount)} subs",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { onSubscribeToggle(creator) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (creator.isSubscribed) Color(0x22FFFFFF)
                                            else ViralRed
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(28.dp)
                                    ) {
                                        Text(
                                            text = if (creator.isSubscribed) "Subscribed" else "Subscribe",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Main Video Feed
            item {
                SectionHeader(
                    title = if (uiState.selectedCategory == "All") "Recommended Videos" else "${uiState.selectedCategory} Feed",
                    subtitle = "Curated high-engagement streams",
                    icon = Icons.Default.PlayCircleFilled
                )
            }

            items(uiState.videos) { video ->
                VideoCard(
                    video = video,
                    onClick = { onVideoClick(video.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
