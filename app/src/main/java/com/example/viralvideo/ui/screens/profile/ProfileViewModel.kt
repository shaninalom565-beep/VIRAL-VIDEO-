package com.example.viralvideo.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viralvideo.data.local.UserAccountEntity
import com.example.viralvideo.data.local.WatchHistoryEntity
import com.example.viralvideo.data.repository.UserRepository
import com.example.viralvideo.data.repository.WatchHistoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserAccountEntity? = null,
    val watchHistory: List<WatchHistoryEntity> = emptyList(),
    val watchLaterList: List<WatchHistoryEntity> = emptyList(),
    val isDarkMode: Boolean = true,
    val isEditProfileOpen: Boolean = false,
    val editNameInput: String = "",
    val editBioInput: String = "",
    val isLoading: Boolean = false
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val watchHistoryRepository: WatchHistoryRepository
) : ViewModel() {

    private val _isDarkMode = MutableStateFlow(true)
    private val _isEditProfileOpen = MutableStateFlow(false)
    private val _editNameInput = MutableStateFlow("")
    private val _editBioInput = MutableStateFlow("")

    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.userAccount,
        watchHistoryRepository.history,
        watchHistoryRepository.watchLater,
        _isDarkMode,
        _isEditProfileOpen
    ) { user, hist, watchLater, darkMode, isEdit ->
        ProfileUiState(
            user = user,
            watchHistory = hist,
            watchLaterList = watchLater,
            isDarkMode = darkMode,
            isEditProfileOpen = isEdit,
            editNameInput = user?.name ?: "",
            editBioInput = user?.bio ?: "",
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true)
    )

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun openEditProfile() {
        _isEditProfileOpen.value = true
    }

    fun closeEditProfile() {
        _isEditProfileOpen.value = false
    }

    fun onNameChanged(name: String) {
        _editNameInput.value = name
    }

    fun onBioChanged(bio: String) {
        _editBioInput.value = bio
    }

    fun saveProfile() {
        viewModelScope.launch {
            val current = userRepository.userAccount.firstOrNull() ?: return@launch
            val updated = current.copy(
                name = _editNameInput.value.ifBlank { current.name },
                bio = _editBioInput.value
            )
            userRepository.updateUser(updated)
            _isEditProfileOpen.value = false
        }
    }

    fun toggle2FA() {
        viewModelScope.launch {
            val current = userRepository.userAccount.firstOrNull() ?: return@launch
            userRepository.updateUser(current.copy(is2FAEnabled = !current.is2FAEnabled))
        }
    }

    fun clearWatchHistory() {
        viewModelScope.launch {
            watchHistoryRepository.clearHistory()
        }
    }
}
