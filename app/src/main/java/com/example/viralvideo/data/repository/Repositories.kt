package com.example.viralvideo.data.repository

import com.example.viralvideo.data.local.*
import kotlinx.coroutines.flow.Flow

class VideoRepository(private val videoDao: VideoDao) {
    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()
    val popularVideos: Flow<List<VideoEntity>> = videoDao.getPopularVideos()
    val trendingVideos: Flow<List<VideoEntity>> = videoDao.getTrendingVideos()
    val shorts: Flow<List<VideoEntity>> = videoDao.getShorts()

    fun getVideosByCategory(category: String): Flow<List<VideoEntity>> {
        return if (category == "All") videoDao.getAllVideos()
        else if (category == "Shorts") videoDao.getShorts()
        else videoDao.getVideosByCategory(category)
    }

    suspend fun getVideoById(id: String): VideoEntity? = videoDao.getVideoById(id)

    fun searchVideos(query: String): Flow<List<VideoEntity>> = videoDao.searchVideos(query)

    suspend fun addVideo(video: VideoEntity) = videoDao.insertVideo(video)

    suspend fun deleteVideo(id: String) = videoDao.deleteVideo(id)

    suspend fun incrementViews(id: String) = videoDao.incrementViews(id)

    suspend fun incrementLikes(id: String) = videoDao.incrementLikes(id)
}

class CreatorRepository(private val creatorDao: CreatorDao) {
    val creators: Flow<List<CreatorEntity>> = creatorDao.getAllCreators()

    suspend fun getCreatorById(id: String): CreatorEntity? = creatorDao.getCreatorById(id)

    suspend fun toggleSubscription(id: String, currentSubscribedStatus: Boolean) {
        creatorDao.toggleSubscription(id, !currentSubscribedStatus)
    }
}

class UserRepository(private val userDao: UserAccountDao) {
    val userAccount: Flow<UserAccountEntity?> = userDao.getUserAccount()

    suspend fun updateUser(user: UserAccountEntity) = userDao.insertOrUpdateUser(user)

    suspend fun setMonetizationStatus(status: Boolean) = userDao.setMonetizationStatus(status)
}

class CommentRepository(private val commentDao: CommentDao) {
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>> = commentDao.getCommentsForVideo(videoId)

    suspend fun addComment(comment: CommentEntity) = commentDao.insertComment(comment)

    suspend fun likeComment(commentId: String) = commentDao.likeComment(commentId)
}

class AdRepository(private val adConfigDao: AdConfigDao) {
    val allAds: Flow<List<AdConfigEntity>> = adConfigDao.getAllAds()
    val activeAds: Flow<List<AdConfigEntity>> = adConfigDao.getActiveAds()

    suspend fun saveAd(ad: AdConfigEntity) = adConfigDao.insertAd(ad)

    suspend fun toggleAdStatus(adId: String, currentActiveStatus: Boolean) {
        adConfigDao.setAdStatus(adId, !currentActiveStatus)
    }
}

class WithdrawRepository(private val withdrawDao: WithdrawDao) {
    val withdrawRequests: Flow<List<WithdrawRequestEntity>> = withdrawDao.getAllWithdrawRequests()

    suspend fun requestWithdraw(request: WithdrawRequestEntity) = withdrawDao.insertWithdrawRequest(request)

    suspend fun updateStatus(requestId: String, status: String) = withdrawDao.updateWithdrawStatus(requestId, status)
}

class WatchHistoryRepository(private val watchHistoryDao: WatchHistoryDao) {
    val history: Flow<List<WatchHistoryEntity>> = watchHistoryDao.getWatchHistory()
    val watchLater: Flow<List<WatchHistoryEntity>> = watchHistoryDao.getWatchLaterList()

    suspend fun addWatchEntry(entry: WatchHistoryEntity) = watchHistoryDao.insertWatchEntry(entry)

    suspend fun toggleWatchLater(videoId: String, isWatchLater: Boolean) = watchHistoryDao.toggleWatchLater(videoId, isWatchLater)

    suspend fun clearHistory() = watchHistoryDao.clearHistory()
}
