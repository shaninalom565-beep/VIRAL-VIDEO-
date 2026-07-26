package com.example.viralvideo.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viralvideo.data.local.AdConfigEntity
import com.example.viralvideo.data.local.CreatorEntity
import com.example.viralvideo.data.local.VideoEntity
import com.example.viralvideo.data.repository.AdRepository
import com.example.viralvideo.data.repository.CreatorRepository
import com.example.viralvideo.data.repository.VideoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedCategory: String = "All",
    val videos: List<VideoEntity> = emptyList(),
    val trendingVideos: List<VideoEntity> = emptyList(),
    val shorts: List<VideoEntity> = emptyList(),
    val creators: List<CreatorEntity> = emptyList(),
    val ads: List<AdConfigEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val videoRepository: VideoRepository,
    private val creatorRepository: CreatorRepository,
    private val adRepository: AdRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")

    private val videoDataFlow = _selectedCategory.flatMapLatest { category ->
        combine(
            videoRepository.getVideosByCategory(category),
            videoRepository.trendingVideos,
            videoRepository.shorts
        ) { videos, trending, shorts ->
            Triple(videos, trending, shorts)
        }
    }

    private val metaDataFlow = combine(
        creatorRepository.creators,
        adRepository.activeAds,
        _selectedCategory,
        _searchQuery
    ) { creators, ads, category, query ->
        QuadData(creators, ads, category, query)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        videoDataFlow,
        metaDataFlow
    ) { (videos, trending, shortsList), meta ->
        val filteredVideos = if (meta.query.isBlank()) videos else videos.filter {
            it.title.contains(meta.query, ignoreCase = true) || it.tags.contains(meta.query, ignoreCase = true)
        }
        HomeUiState(
            selectedCategory = meta.category,
            videos = filteredVideos,
            trendingVideos = trending,
            shorts = shortsList,
            creators = meta.creators,
            ads = meta.ads,
            searchQuery = meta.query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleCreatorSubscribe(creator: CreatorEntity) {
        viewModelScope.launch {
            creatorRepository.toggleSubscription(creator.id, creator.isSubscribed)
        }
    }

    private data class QuadData(
        val creators: List<CreatorEntity>,
        val ads: List<AdConfigEntity>,
        val category: String,
        val query: String
    )
}
