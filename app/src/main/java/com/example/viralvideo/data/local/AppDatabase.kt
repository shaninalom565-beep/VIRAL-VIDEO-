package com.example.viralvideo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        VideoEntity::class,
        CreatorEntity::class,
        UserAccountEntity::class,
        CommentEntity::class,
        AdConfigEntity::class,
        WithdrawRequestEntity::class,
        WatchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun videoDao(): VideoDao
    abstract fun creatorDao(): CreatorDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun commentDao(): CommentDao
    abstract fun adConfigDao(): AdConfigDao
    abstract fun withdrawDao(): WithdrawDao
    abstract fun watchHistoryDao(): WatchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "viral_video_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateDatabase(database)
                    }
                }
            }
        }

        private suspend fun prepopulateDatabase(db: AppDatabase) {
            // Seed User
            db.userAccountDao().insertOrUpdateUser(
                UserAccountEntity(
                    id = "current_user",
                    name = "Alex Vance",
                    handle = "@alexvance_viral",
                    email = "alex.vance@viralvideo.io",
                    avatarUrl = "https://picsum.photos/seed/user_alex/200/200",
                    coverUrl = "https://picsum.photos/seed/cover_alex/800/300",
                    bio = "Tech Creator & Streamer. Creating the next wave of viral content! 🎬✨",
                    isMonetized = true,
                    subscribersCount = 680,
                    totalViewsCount = 58920,
                    publicVideosCount = 11,
                    accountAgeDays = 42,
                    totalEarnings = 540.80,
                    monthlyRevenue = 210.40,
                    dailyRevenue = 28.50,
                    pendingWithdraw = 150.00
                )
            )

            // Seed Creators
            val creators = listOf(
                CreatorEntity(
                    id = "c1",
                    name = "CyberTech Labs",
                    handle = "@cybertech",
                    avatarUrl = "https://picsum.photos/seed/creator_cyber/200/200",
                    bannerUrl = "https://picsum.photos/seed/banner_cyber/800/300",
                    bio = "Unboxing future tech, AI gadgets, and quantum computing breakdowns.",
                    subscribersCount = 1250000,
                    totalViews = 84500000,
                    isVerified = true
                ),
                CreatorEntity(
                    id = "c2",
                    name = "Epic Gamer Nation",
                    handle = "@epicgamer",
                    avatarUrl = "https://picsum.photos/seed/creator_gamer/200/200",
                    bannerUrl = "https://picsum.photos/seed/banner_gamer/800/300",
                    bio = "Daily esports highlights, Unreal Engine 5 gameplay, and pro strategies.",
                    subscribersCount = 890000,
                    totalViews = 45200000,
                    isVerified = true
                ),
                CreatorEntity(
                    id = "c3",
                    name = "BeatDrop Music",
                    handle = "@beatdrop",
                    avatarUrl = "https://picsum.photos/seed/creator_music/200/200",
                    bannerUrl = "https://picsum.photos/seed/banner_music/800/300",
                    bio = "Original lo-fi beats, synthwave music videos, and studio session vlogs.",
                    subscribersCount = 2100000,
                    totalViews = 156000000,
                    isVerified = true
                ),
                CreatorEntity(
                    id = "c4",
                    name = "CodeWithViral",
                    handle = "@codewithviral",
                    avatarUrl = "https://picsum.photos/seed/creator_code/200/200",
                    bannerUrl = "https://picsum.photos/seed/banner_code/800/300",
                    bio = "Learn Jetpack Compose, AI engineering, and full-stack development.",
                    subscribersCount = 430000,
                    totalViews = 18900000,
                    isVerified = true
                )
            )
            db.creatorDao().insertCreators(creators)

            // Seed Videos (Standard Long Form & Shorts)
            val videos = listOf(
                VideoEntity(
                    id = "v1",
                    title = "The AI Revolution in 2026: Next Gen Neural Chips Revealed!",
                    description = "In this video, we break down the latest breakthrough in bio-silicon neural chips, real-time holographic rendering, and instant AI video synthesis.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    thumbnailUrl = "https://picsum.photos/seed/tech_ai_2026/600/340",
                    category = "Technology",
                    duration = "14:25",
                    viewsCount = 1489000,
                    likesCount = 89200,
                    dislikesCount = 120,
                    uploadDate = "2 hours ago",
                    creatorId = "c1",
                    creatorName = "CyberTech Labs",
                    creatorAvatar = "https://picsum.photos/seed/creator_cyber/200/200",
                    isShort = false,
                    isViral = true,
                    isTrending = true,
                    isFeatured = true,
                    tags = "AI, Tech, Future, Hardware, Chips"
                ),
                VideoEntity(
                    id = "v2",
                    title = "Unreal Engine 6 Gameplay Test: Photo-Realistic Open World!",
                    description = "Testing the early alpha build of Unreal Engine 6 with dynamic global illumination, nanite foliage, and 240 FPS ray-tracing performance.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    thumbnailUrl = "https://picsum.photos/seed/gaming_unreal6/600/340",
                    category = "Gaming",
                    duration = "22:10",
                    viewsCount = 980500,
                    likesCount = 67400,
                    uploadDate = "5 hours ago",
                    creatorId = "c2",
                    creatorName = "Epic Gamer Nation",
                    creatorAvatar = "https://picsum.photos/seed/creator_gamer/200/200",
                    isShort = false,
                    isViral = true,
                    isTrending = true,
                    tags = "Gaming, UnrealEngine, Graphics, 4K"
                ),
                VideoEntity(
                    id = "v3",
                    title = "Cyberpunk Midnight Beats - 24/7 Chill Synthwave Mix",
                    description = "Relax, study, or code with deep atmospheric cyberpunk synth beats and neon visuals recorded live in Neo-Tokyo.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    thumbnailUrl = "https://picsum.photos/seed/synthwave_lofi/600/340",
                    category = "Music",
                    duration = "45:00",
                    viewsCount = 3450000,
                    likesCount = 192000,
                    uploadDate = "1 day ago",
                    creatorId = "c3",
                    creatorName = "BeatDrop Music",
                    creatorAvatar = "https://picsum.photos/seed/creator_music/200/200",
                    isShort = false,
                    isViral = false,
                    isTrending = true,
                    tags = "Music, Synthwave, Lofi, Cyberpunk"
                ),
                VideoEntity(
                    id = "v4",
                    title = "Building a Full Production Android App with Jetpack Compose in 20 Minutes",
                    description = "Master Jetpack Compose state management, Room database integration, glassmorphism UI, and clean architecture in one complete masterclass.",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    thumbnailUrl = "https://picsum.photos/seed/compose_masterclass/600/340",
                    category = "Education",
                    duration = "28:40",
                    viewsCount = 520000,
                    likesCount = 41500,
                    uploadDate = "3 days ago",
                    creatorId = "c4",
                    creatorName = "CodeWithViral",
                    creatorAvatar = "https://picsum.photos/seed/creator_code/200/200",
                    isShort = false,
                    isViral = false,
                    isTrending = false,
                    tags = "Android, Kotlin, JetpackCompose, Coding"
                ),
                // Shorts
                VideoEntity(
                    id = "s1",
                    title = "Insane 3D Hologram Phone Case! 🤯 #Shorts #Tech",
                    description = "This phone case literally projects 3D interactive graphics into thin air! Would you buy this?",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                    thumbnailUrl = "https://picsum.photos/seed/short_hologram/400/700",
                    category = "Shorts",
                    duration = "0:30",
                    viewsCount = 5890000,
                    likesCount = 480000,
                    uploadDate = "Yesterday",
                    creatorId = "c1",
                    creatorName = "CyberTech Labs",
                    creatorAvatar = "https://picsum.photos/seed/creator_cyber/200/200",
                    isShort = true,
                    isViral = true,
                    soundName = "CyberTech Beats Vol. 1",
                    aspectRatio = "9:16"
                ),
                VideoEntity(
                    id = "s2",
                    title = "Impossible FPS Clutch Play 🎯🔥 #Shorts #Gaming",
                    description = "1v5 clutch with 1hp remaining in pro tournament final round!",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoylikes.mp4",
                    thumbnailUrl = "https://picsum.photos/seed/short_clutch/400/700",
                    category = "Shorts",
                    duration = "0:45",
                    viewsCount = 3120000,
                    likesCount = 295000,
                    uploadDate = "2 days ago",
                    creatorId = "c2",
                    creatorName = "Epic Gamer Nation",
                    creatorAvatar = "https://picsum.photos/seed/creator_gamer/200/200",
                    isShort = true,
                    isViral = true,
                    soundName = "Pro Esports Sound FX",
                    aspectRatio = "9:16"
                ),
                VideoEntity(
                    id = "s3",
                    title = "Live Studio Drop: 10 Second Beat Challenge 🥁🎧",
                    description = "Making a hit synthwave loop in under 10 seconds using vintage hardware!",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                    thumbnailUrl = "https://picsum.photos/seed/short_beatbox/400/700",
                    category = "Shorts",
                    duration = "0:15",
                    viewsCount = 1890000,
                    likesCount = 162000,
                    uploadDate = "3 hours ago",
                    creatorId = "c3",
                    creatorName = "BeatDrop Music",
                    creatorAvatar = "https://picsum.photos/seed/creator_music/200/200",
                    isShort = true,
                    isViral = false,
                    soundName = "BeatDrop Original Audio",
                    aspectRatio = "9:16"
                )
            )
            db.videoDao().insertVideos(videos)

            // Seed Comments for video v1
            val comments = listOf(
                CommentEntity(
                    id = "cm1",
                    videoId = "v1",
                    userId = "u_user1",
                    userName = "Elena Rostova",
                    userAvatar = "https://picsum.photos/seed/user_elena/100/100",
                    text = "This neural chip architecture is mind-blowing! The power efficiency gains are unbelievable.",
                    likesCount = 1420,
                    timestamp = "1 hour ago",
                    isPinned = true
                ),
                CommentEntity(
                    id = "cm2",
                    videoId = "v1",
                    userId = "u_user2",
                    userName = "Marcus Chen",
                    userAvatar = "https://picsum.photos/seed/user_marcus/100/100",
                    text = "VIRAL VIDEO player quality and streaming speed is crisp! Loving the 4K playback.",
                    likesCount = 385,
                    timestamp = "45 minutes ago"
                )
            )
            db.commentDao().insertComments(comments)

            // Seed Ad Placements
            val ads = listOf(
                AdConfigEntity(
                    id = "ad_top_banner",
                    title = "Adsterra Premium Video Gear",
                    format = "Native Banner",
                    position = "Homepage Top",
                    isActive = true,
                    targetDevice = "All",
                    bannerUrl = "https://picsum.photos/seed/ad_camera_gear/800/150",
                    impressions = 48200,
                    clicks = 2450
                ),
                AdConfigEntity(
                    id = "ad_player_preroll",
                    title = "SocialBar Sponsor: Cyber VPN Pro",
                    format = "Social Bar",
                    position = "Video Player",
                    isActive = true,
                    targetDevice = "All",
                    bannerUrl = "https://picsum.photos/seed/ad_cyber_vpn/600/100",
                    impressions = 89100,
                    clicks = 6120
                ),
                AdConfigEntity(
                    id = "ad_sidebar_pop",
                    title = "Direct Link Gaming Hardware",
                    format = "Direct Link",
                    position = "Sidebar",
                    isActive = true,
                    targetDevice = "Desktop Only",
                    bannerUrl = "https://picsum.photos/seed/ad_gaming_mouse/300/250",
                    impressions = 19400,
                    clicks = 890
                )
            )
            db.adConfigDao().insertAds(ads)

            // Seed Withdraw Requests
            val withdraws = listOf(
                WithdrawRequestEntity(
                    id = "wr_101",
                    userId = "current_user",
                    amount = 250.00,
                    method = "PayPal",
                    accountDetails = "alex.vance@viralvideo.io",
                    status = "Approved",
                    requestDate = "Jul 15, 2026"
                ),
                WithdrawRequestEntity(
                    id = "wr_102",
                    userId = "current_user",
                    amount = 120.00,
                    method = "Bank Transfer",
                    accountDetails = "Wire ****4829",
                    status = "Pending",
                    requestDate = "Jul 24, 2026"
                )
            )
            db.withdrawDao().insertWithdrawRequest(withdraws[0])
            db.withdrawDao().insertWithdrawRequest(withdraws[1])
        }
    }
}
