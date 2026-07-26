package com.example.viralvideo.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viralvideo.data.local.AdConfigEntity
import com.example.viralvideo.data.local.VideoEntity
import com.example.viralvideo.data.local.WithdrawRequestEntity
import com.example.viralvideo.data.repository.AdRepository
import com.example.viralvideo.data.repository.VideoRepository
import com.example.viralvideo.data.repository.WithdrawRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdminPanelUiState(
    val ads: List<AdConfigEntity> = emptyList(),
    val withdrawRequests: List<WithdrawRequestEntity> = emptyList(),
    val videos: List<VideoEntity> = emptyList(),
    val totalUsers: Int = 142800,
    val totalCreators: Int = 3840,
    val totalPlatformRevenue: Double = 89450.00,
    val isNewAdDialogOpen: Boolean = false,
    val newAdTitle: String = "",
    val newAdFormat: String = "Native Banner",
    val newAdPosition: String = "Sidebar",
    val isLoading: Boolean = false
)

class AdminPanelViewModel(
    private val adRepository: AdRepository,
    private val withdrawRepository: WithdrawRepository,
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _isNewAdDialogOpen = MutableStateFlow(false)
    private val _newAdTitle = MutableStateFlow("")
    private val _newAdFormat = MutableStateFlow("Native Banner")
    private val _newAdPosition = MutableStateFlow("Sidebar")

    val uiState: StateFlow<AdminPanelUiState> = combine(
        adRepository.allAds,
        withdrawRepository.withdrawRequests,
        videoRepository.allVideos,
        _isNewAdDialogOpen,
        _newAdTitle
    ) { adsList, withdraws, vids, isAdOpen, title ->
        AdminPanelUiState(
            ads = adsList,
            withdrawRequests = withdraws,
            videos = vids,
            isNewAdDialogOpen = isAdOpen,
            newAdTitle = title,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AdminPanelUiState(isLoading = true)
    )

    fun toggleAdStatus(ad: AdConfigEntity) {
        viewModelScope.launch {
            adRepository.toggleAdStatus(ad.id, ad.isActive)
        }
    }

    fun approveWithdraw(requestId: String) {
        viewModelScope.launch {
            withdrawRepository.updateStatus(requestId, "Approved")
        }
    }

    fun rejectWithdraw(requestId: String) {
        viewModelScope.launch {
            withdrawRepository.updateStatus(requestId, "Rejected")
        }
    }

    fun openNewAdDialog() { _isNewAdDialogOpen.value = true }
    fun closeNewAdDialog() { _isNewAdDialogOpen.value = false }

    fun onNewAdTitleChanged(title: String) { _newAdTitle.value = title }
    fun onNewAdFormatChanged(format: String) { _newAdFormat.value = format }
    fun onNewAdPositionChanged(pos: String) { _newAdPosition.value = pos }

    fun submitCreateAd() {
        val title = _newAdTitle.value
        if (title.isBlank()) return

        viewModelScope.launch {
            val newAd = AdConfigEntity(
                id = "ad_${System.currentTimeMillis()}",
                title = title,
                format = _newAdFormat.value,
                position = _newAdPosition.value,
                isActive = true,
                targetDevice = "All",
                impressions = 100,
                clicks = 12
            )
            adRepository.saveAd(newAd)
            _newAdTitle.value = ""
            _isNewAdDialogOpen.value = false
        }
    }

    fun deleteVideo(videoId: String) {
        viewModelScope.launch {
            videoRepository.deleteVideo(videoId)
        }
    }
}
