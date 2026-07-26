package com.example.viralvideo.ui.screens.creator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viralvideo.data.local.UserAccountEntity
import com.example.viralvideo.data.local.VideoEntity
import com.example.viralvideo.data.local.WithdrawRequestEntity
import com.example.viralvideo.data.repository.UserRepository
import com.example.viralvideo.data.repository.VideoRepository
import com.example.viralvideo.data.repository.WithdrawRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CreatorStudioUiState(
    val user: UserAccountEntity? = null,
    val myVideos: List<VideoEntity> = emptyList(),
    val withdrawHistory: List<WithdrawRequestEntity> = emptyList(),
    val newVideoTitle: String = "",
    val newVideoDescription: String = "",
    val newVideoCategory: String = "Technology",
    val newVideoTags: String = "Viral, Tech, 2026",
    val isUploadDialogOpen: Boolean = false,
    val isWithdrawDialogOpen: Boolean = false,
    val withdrawAmountInput: String = "100",
    val withdrawMethod: String = "PayPal",
    val withdrawAccountInput: String = "alex.vance@viralvideo.io",
    val isLoading: Boolean = false
)

class CreatorStudioViewModel(
    private val userRepository: UserRepository,
    private val videoRepository: VideoRepository,
    private val withdrawRepository: WithdrawRepository
) : ViewModel() {

    private val _newVideoTitle = MutableStateFlow("")
    private val _newVideoDescription = MutableStateFlow("")
    private val _newVideoCategory = MutableStateFlow("Technology")
    private val _newVideoTags = MutableStateFlow("Viral, Tech, 2026")
    private val _isUploadDialogOpen = MutableStateFlow(false)
    private val _isWithdrawDialogOpen = MutableStateFlow(false)
    private val _withdrawAmountInput = MutableStateFlow("100")
    private val _withdrawMethod = MutableStateFlow("PayPal")
    private val _withdrawAccountInput = MutableStateFlow("alex.vance@viralvideo.io")

    private val dbDataFlow = combine(
        userRepository.userAccount,
        videoRepository.allVideos,
        withdrawRepository.withdrawRequests
    ) { user, videos, withdraws ->
        Triple(user, videos, withdraws)
    }

    private val dialogDataFlow = combine(
        _isUploadDialogOpen,
        _isWithdrawDialogOpen,
        _newVideoTitle,
        _withdrawAmountInput
    ) { isUploadOpen, isWithdrawOpen, title, withdrawAmt ->
        DialogData(isUploadOpen, isWithdrawOpen, title, withdrawAmt)
    }

    val uiState: StateFlow<CreatorStudioUiState> = combine(
        dbDataFlow,
        dialogDataFlow
    ) { (user, videos, withdraws), dialogs ->
        val myVids = videos.filter { it.creatorId == "current_user" || it.creatorName == (user?.name ?: "Alex Vance") }
        CreatorStudioUiState(
            user = user,
            myVideos = myVids,
            withdrawHistory = withdraws,
            newVideoTitle = dialogs.title,
            isUploadDialogOpen = dialogs.isUploadOpen,
            isWithdrawDialogOpen = dialogs.isWithdrawOpen,
            withdrawAmountInput = dialogs.withdrawAmt,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CreatorStudioUiState(isLoading = true)
    )

    fun openUploadDialog() { _isUploadDialogOpen.value = true }
    fun closeUploadDialog() { _isUploadDialogOpen.value = false }

    fun openWithdrawDialog() { _isWithdrawDialogOpen.value = true }
    fun closeWithdrawDialog() { _isWithdrawDialogOpen.value = false }

    fun onTitleChanged(text: String) { _newVideoTitle.value = text }
    fun onDescriptionChanged(text: String) { _newVideoDescription.value = text }
    fun onCategoryChanged(cat: String) { _newVideoCategory.value = cat }
    fun onTagsChanged(tags: String) { _newVideoTags.value = tags }

    fun onWithdrawAmountChanged(amt: String) { _withdrawAmountInput.value = amt }
    fun onWithdrawMethodChanged(method: String) { _withdrawMethod.value = method }
    fun onWithdrawAccountChanged(acc: String) { _withdrawAccountInput.value = acc }

    fun submitUploadVideo() {
        val title = _newVideoTitle.value
        if (title.isBlank()) return

        viewModelScope.launch {
            val user = userRepository.userAccount.firstOrNull() ?: return@launch
            val newVideo = VideoEntity(
                id = "vid_${System.currentTimeMillis()}",
                title = title,
                description = _newVideoDescription.value.ifBlank { "Uploaded via Creator Studio" },
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = "https://picsum.photos/seed/myupload_${System.currentTimeMillis()}/600/340",
                category = _newVideoCategory.value,
                duration = "08:15",
                viewsCount = 1,
                likesCount = 1,
                uploadDate = "Just now",
                creatorId = "current_user",
                creatorName = user.name,
                creatorAvatar = user.avatarUrl,
                tags = _newVideoTags.value
            )
            videoRepository.addVideo(newVideo)

            // Update user video count
            userRepository.updateUser(user.copy(publicVideosCount = user.publicVideosCount + 1))
            _newVideoTitle.value = ""
            _newVideoDescription.value = ""
            _isUploadDialogOpen.value = false
        }
    }

    fun submitWithdrawRequest() {
        val amt = _withdrawAmountInput.value.toDoubleOrNull() ?: 100.0
        viewModelScope.launch {
            val request = WithdrawRequestEntity(
                id = "wr_${System.currentTimeMillis()}",
                userId = "current_user",
                amount = amt,
                method = _withdrawMethod.value,
                accountDetails = _withdrawAccountInput.value,
                status = "Pending",
                requestDate = "Today"
            )
            withdrawRepository.requestWithdraw(request)
            _isWithdrawDialogOpen.value = false
        }
    }

    fun deleteVideo(id: String) {
        viewModelScope.launch {
            videoRepository.deleteVideo(id)
        }
    }

    private data class DialogData(
        val isUploadOpen: Boolean,
        val isWithdrawOpen: Boolean,
        val title: String,
        val withdrawAmt: String
    )
}
