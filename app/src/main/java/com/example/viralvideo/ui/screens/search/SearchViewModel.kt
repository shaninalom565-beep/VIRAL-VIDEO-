package com.example.viralvideo.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viralvideo.data.local.VideoEntity
import com.example.viralvideo.data.repository.VideoRepository
import kotlinx.coroutines.flow.*

data class SearchUiState(
    val query: String = "",
    val results: List<VideoEntity> = emptyList(),
    val trendingSearches: List<String> = listOf("AI 2026", "Unreal Engine 6", "Lo-Fi Beats", "Jetpack Compose", "3D Hologram", "Esports Clutch"),
    val isLoading: Boolean = false
)

class SearchViewModel(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")

    val uiState: StateFlow<SearchUiState> = _query.flatMapLatest { q ->
        if (q.isBlank()) {
            videoRepository.allVideos.map { vids ->
                SearchUiState(query = q, results = vids, isLoading = false)
            }
        } else {
            videoRepository.searchVideos(q).map { vids ->
                SearchUiState(query = q, results = vids, isLoading = false)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState(isLoading = true)
    )

    fun onQueryChanged(q: String) {
        _query.value = q
    }
}
