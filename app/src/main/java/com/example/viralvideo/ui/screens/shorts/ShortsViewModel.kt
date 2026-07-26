package com.example.viralvideo.ui.screens.shorts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viralvideo.data.local.VideoEntity
import com.example.viralvideo.data.repository.VideoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ShortsUiState(
    val shortsList: List<VideoEntity> = emptyList(),
    val currentIndex: Int = 0,
    val isPlaying: Boolean = true,
    val isLiked: Boolean = false,
    val commentCount: Int = 1840,
    val isLoading: Boolean = false
)

class ShortsViewModel(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _currentIndex = MutableStateFlow(0)
    private val _isPlaying = MutableStateFlow(true)
    private val _isLiked = MutableStateFlow(false)

    val uiState: StateFlow<ShortsUiState> = combine(
        videoRepository.shorts,
        _currentIndex,
        _isPlaying,
        _isLiked
    ) { shorts, index, playing, liked ->
        ShortsUiState(
            shortsList = shorts,
            currentIndex = index.coerceIn(0, (shorts.size - 1).coerceAtLeast(0)),
            isPlaying = playing,
            isLiked = liked,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShortsUiState(isLoading = true)
    )

    fun nextShort() {
        val list = uiState.value.shortsList
        if (list.isNotEmpty()) {
            _currentIndex.value = (_currentIndex.value + 1) % list.size
            _isLiked.value = false
        }
    }

    fun prevShort() {
        val list = uiState.value.shortsList
        if (list.isNotEmpty()) {
            _currentIndex.value = if (_currentIndex.value == 0) list.size - 1 else _currentIndex.value - 1
            _isLiked.value = false
        }
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun toggleLike() {
        val wasLiked = _isLiked.value
        _isLiked.value = !wasLiked
        val current = uiState.value.shortsList.getOrNull(_currentIndex.value)
        if (current != null && !wasLiked) {
            viewModelScope.launch {
                videoRepository.incrementLikes(current.id)
            }
        }
    }
}
