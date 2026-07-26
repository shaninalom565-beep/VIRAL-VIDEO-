package com.example.viralvideo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val category: String, // Trending, Shorts, Gaming, Music, Tech, Education, News, Entertainment
    val duration: String,
    val viewsCount: Long,
    val likesCount: Long,
    val dislikesCount: Long = 0,
    val uploadDate: String,
    val creatorId: String,
    val creatorName: String,
    val creatorAvatar: String,
    val isShort: Boolean = false,
    val isViral: Boolean = false,
    val isTrending: Boolean = false,
    val isFeatured: Boolean = false,
    val tags: String = "",
    val soundName: String = "Original Sound",
    val aspectRatio: String = "16:9"
)

@Entity(tableName = "creators")
data class CreatorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val handle: String,
    val avatarUrl: String,
    val bannerUrl: String,
    val bio: String,
    val subscribersCount: Long,
    val totalViews: Long,
    val isVerified: Boolean = true,
    val joinedDate: String = "Jan 2024",
    val email: String = "",
    val isSubscribed: Boolean = false
)

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey val id: String = "current_user",
    val name: String = "Alex Vance",
    val handle: String = "@alexvance",
    val email: String = "alex.vance@viralvideo.io",
    val avatarUrl: String = "https://picsum.photos/seed/user_alex/200/200",
    val coverUrl: String = "https://picsum.photos/seed/cover_alex/800/300",
    val bio: String = "Digital Creator | Tech & Gaming Reviews 🚀",
    val isMonetized: Boolean = false,
    val subscribersCount: Long = 620,
    val totalViewsCount: Long = 58400,
    val publicVideosCount: Int = 12,
    val accountAgeDays: Int = 45,
    val totalEarnings: Double = 342.50,
    val monthlyRevenue: Double = 128.75,
    val dailyRevenue: Double = 14.20,
    val pendingWithdraw: Double = 120.00,
    val is2FAEnabled: Boolean = true,
    val isIdentityVerified: Boolean = true,
    val isGoogleLinked: Boolean = true
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val text: String,
    val likesCount: Int = 0,
    val timestamp: String,
    val isPinned: Boolean = false,
    val isLiked: Boolean = false
)

@Entity(tableName = "ad_configs")
data class AdConfigEntity(
    @PrimaryKey val id: String,
    val title: String,
    val format: String, // Banner, Native, Social Bar, Popunder, Pre-roll
    val position: String, // Homepage Top, Header, Sidebar, Video Player, Search Page
    val isActive: Boolean = true,
    val targetDevice: String = "All", // Mobile Only, Desktop Only, All
    val clickUrl: String = "https://viralvideo.io/ads/promo",
    val bannerUrl: String = "",
    val impressions: Int = 14200,
    val clicks: Int = 890
)

@Entity(tableName = "withdraw_requests")
data class WithdrawRequestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val method: String, // PayPal, Bank Transfer, Crypto, Stripe
    val accountDetails: String,
    val status: String, // Pending, Approved, Rejected
    val requestDate: String
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val id: String,
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val creatorName: String,
    val watchedAt: Long = System.currentTimeMillis(),
    val progressSec: Int = 0,
    val isWatchLater: Boolean = false
)
