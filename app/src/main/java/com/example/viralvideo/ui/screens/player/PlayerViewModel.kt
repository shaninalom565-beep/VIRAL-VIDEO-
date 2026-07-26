package com.example.viralvideo.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viralvideo.data.local.CommentEntity
import com.example.viralvideo.data.local.VideoEntity
import com.example.viralvideo.data.local.WatchHistoryEntity
import com.example.viralvideo.data.repository.CommentRepository
import com.example.viralvideo.data.repository.VideoRepository
import com.example.viralvideo.data.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentVideo: VideoEntity? = null,
    val relatedVideos: List<VideoEntity> = emptyList(),
    val comments: List<CommentEntity> = emptyList(),
    val isPlaying: Boolean = true,
    val currentProgressSec: Int = 12,
    val totalDurationSec: Int = 865,
    val playbackSpeed: Float = 1.0f,
    val quality: String = "1080p HD",
    val isSubtitlesOn: Boolean = false,
    val isTheaterMode: Boolean = false,
    val isPiPMode: Boolean = false,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSavedWatchLater: Boolean = false,
    val commentInputText: String = "",
    val isLoading: Boolean = false
)

class PlayerViewModel(
    private val videoId: String,
    private val videoRepository: VideoRepository,
    private val commentRepository: CommentRepository,
    private val watchHistoryRepository: WatchHistoryRepository
) : ViewModel() {

    private val _isPlaying = MutableStateFlow(true)
    private val _currentProgressSec = MutableStateFlow(12)
    private val _playbackSpeed = MutableStateFlow(1.0f)
    private val _quality = MutableStateFlow("1080p HD")
    private val _isSubtitlesOn = MutableStateFlow(false)
    private val _isTheaterMode = MutableStateFlow(false)
    private val _isPiPMode = MutableStateFlow(false)
    private val _isLiked = MutableStateFlow(false)
    private val _isDisliked = MutableStateFlow(false)
    private val _isSavedWatchLater = MutableStateFlow(false)
    private val _commentInputText = MutableStateFlow("")

    private val videoAndCommentsFlow = combine(
        flow { emit(videoRepository.getVideoById(videoId)) },
        videoRepository.allVideos,
        commentRepository.getCommentsForVideo(videoId)
    ) { video, allVids, commentsList ->
        Triple(video, allVids, commentsList)
    }

    private val playbackStateFlow = combine(
        _isPlaying,
        _currentProgressSec,
        _playbackSpeed,
        _quality
    ) { playing, progress, speed, qual ->
        PlaybackStateData(playing, progress, speed, qual)
    }

    private val playerControlsFlow = combine(
        _isSubtitlesOn,
        _isTheaterMode,
        _isPiPMode,
        _isLiked,
        _commentInputText
    ) { subs, theater, pip, liked, commentText ->
        PlayerControlsData(subs, theater, pip, liked, commentText)
    }

    val uiState: StateFlow<PlayerUiState> = combine(
        videoAndCommentsFlow,
        playbackStateFlow,
        playerControlsFlow
    ) { (video, allVids, commentsList), pb, ctrl ->
        val related = allVids.filter { it.id != videoId && !it.isShort }

        // Track in watch history
        video?.let {
            viewModelScope.launch {
                watchHistoryRepository.addWatchEntry(
                    WatchHistoryEntity(
                        id = "hist_${it.id}",
                        videoId = it.id,
                        title = it.title,
                        thumbnailUrl = it.thumbnailUrl,
                        creatorName = it.creatorName,
                        progressSec = pb.progress
                    )
                )
            }
        }

        PlayerUiState(
            currentVideo = video,
            relatedVideos = related,
            comments = commentsList,
            isPlaying = pb.playing,
            currentProgressSec = pb.progress,
            playbackSpeed = pb.speed,
            quality = pb.quality,
            isSubtitlesOn = ctrl.subtitles,
            isTheaterMode = ctrl.theater,
            isPiPMode = ctrl.pip,
            isLiked = ctrl.liked,
            commentInputText = ctrl.commentText,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayerUiState(isLoading = true)
    )

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun seekTo(seconds: Int) {
        _currentProgressSec.value = seconds
    }

    fun setSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    fun setQuality(qual: String) {
        _quality.value = qual
    }

    fun toggleSubtitles() {
        _isSubtitlesOn.value = !_isSubtitlesOn.value
    }

    fun toggleTheaterMode() {
        _isTheaterMode.value = !_isTheaterMode.value
    }

    fun togglePiP() {
        _isPiPMode.value = !_isPiPMode.value
    }

    fun toggleLike() {
        val wasLiked = _isLiked.value
        _isLiked.value = !wasLiked
        if (!wasLiked) {
            viewModelScope.launch {
                videoRepository.incrementLikes(videoId)
            }
        }
    }

    fun toggleWatchLater() {
        val next = !_isSavedWatchLater.value
        _isSavedWatchLater.value = next
        viewModelScope.launch {
            watchHistoryRepository.toggleWatchLater(videoId, next)
        }
    }

    fun onCommentInputChanged(text: String) {
        _commentInputText.value = text
    }

    fun submitComment() {
        val text = _commentInputText.value
        if (text.isBlank()) return

        viewModelScope.launch {
            commentRepository.addComment(
                CommentEntity(
                    id = "cm_${System.currentTimeMillis()}",
                    videoId = videoId,
                    userId = "current_user",
                    userName = "Alex Vance",
                    userAvatar = "https://picsum.photos/seed/user_alex/200/200",
                    text = text,
                    likesCount = 0,
                    timestamp = "Just now"
                )
            )
            _commentInputText.value = ""
        }
    }

    fun likeComment(commentId: String) {
        viewModelScope.launch {
            commentRepository.likeComment(commentId)
        }
    }

    private data class PlaybackStateData(
        val playing: Boolean,
        val progress: Int,
        val speed: Float,
        val quality: String
    )

    private data class PlayerControlsData(
        val subtitles: Boolean,
        val theater: Boolean,
        val pip: Boolean,
        val liked: Boolean,
        val commentText: String
    )
}
