package com.example.viralvideo.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isShort = 0 ORDER BY viewsCount DESC")
    fun getPopularVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isTrending = 1 OR isViral = 1")
    fun getTrendingVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isShort = 1")
    fun getShorts(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE category = :category AND isShort = 0")
    fun getVideosByCategory(category: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :videoId LIMIT 1")
    suspend fun getVideoById(videoId: String): VideoEntity?

    @Query("SELECT * FROM videos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchVideos(query: String): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Query("DELETE FROM videos WHERE id = :videoId")
    suspend fun deleteVideo(videoId: String)

    @Query("UPDATE videos SET viewsCount = viewsCount + 1 WHERE id = :videoId")
    suspend fun incrementViews(videoId: String)

    @Query("UPDATE videos SET likesCount = likesCount + 1 WHERE id = :videoId")
    suspend fun incrementLikes(videoId: String)
}

@Dao
interface CreatorDao {
    @Query("SELECT * FROM creators")
    fun getAllCreators(): Flow<List<CreatorEntity>>

    @Query("SELECT * FROM creators WHERE id = :creatorId LIMIT 1")
    suspend fun getCreatorById(creatorId: String): CreatorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreators(creators: List<CreatorEntity>)

    @Query("UPDATE creators SET isSubscribed = :subscribed, subscribersCount = subscribersCount + (CASE WHEN :subscribed THEN 1 ELSE -1 END) WHERE id = :creatorId")
    suspend fun toggleSubscription(creatorId: String, subscribed: Boolean)
}

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_account WHERE id = 'current_user' LIMIT 1")
    fun getUserAccount(): Flow<UserAccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserAccountEntity)

    @Query("UPDATE user_account SET isMonetized = :status WHERE id = 'current_user'")
    suspend fun setMonetizationStatus(status: Boolean)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY isPinned DESC, likesCount DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("UPDATE comments SET likesCount = likesCount + 1, isLiked = 1 WHERE id = :commentId")
    suspend fun likeComment(commentId: String)
}

@Dao
interface AdConfigDao {
    @Query("SELECT * FROM ad_configs")
    fun getAllAds(): Flow<List<AdConfigEntity>>

    @Query("SELECT * FROM ad_configs WHERE isActive = 1")
    fun getActiveAds(): Flow<List<AdConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAd(ad: AdConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAds(ads: List<AdConfigEntity>)

    @Query("UPDATE ad_configs SET isActive = :isActive WHERE id = :adId")
    suspend fun setAdStatus(adId: String, isActive: Boolean)
}

@Dao
interface WithdrawDao {
    @Query("SELECT * FROM withdraw_requests ORDER BY requestDate DESC")
    fun getAllWithdrawRequests(): Flow<List<WithdrawRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawRequest(request: WithdrawRequestEntity)

    @Query("UPDATE withdraw_requests SET status = :status WHERE id = :requestId")
    suspend fun updateWithdrawStatus(requestId: String, status: String)
}

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC")
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE isWatchLater = 1 ORDER BY watchedAt DESC")
    fun getWatchLaterList(): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchEntry(entry: WatchHistoryEntity)

    @Query("UPDATE watch_history SET isWatchLater = :watchLater WHERE videoId = :videoId")
    suspend fun toggleWatchLater(videoId: String, watchLater: Boolean)

    @Query("DELETE FROM watch_history")
    suspend fun clearHistory()
}
