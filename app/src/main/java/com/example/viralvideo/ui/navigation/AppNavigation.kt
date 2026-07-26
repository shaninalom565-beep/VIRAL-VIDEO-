package com.example.viralvideo.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.theme.ViralRed
import com.example.viralvideo.data.local.AppDatabase
import com.example.viralvideo.data.repository.*
import com.example.viralvideo.ui.screens.admin.AdminPanelScreen
import com.example.viralvideo.ui.screens.admin.AdminPanelViewModel
import com.example.viralvideo.ui.screens.creator.CreatorStudioScreen
import com.example.viralvideo.ui.screens.creator.CreatorStudioViewModel
import com.example.viralvideo.ui.screens.home.HomeScreen
import com.example.viralvideo.ui.screens.home.HomeViewModel
import com.example.viralvideo.ui.screens.player.VideoPlayerScreen
import com.example.viralvideo.ui.screens.player.PlayerViewModel
import com.example.viralvideo.ui.screens.profile.ProfileScreen
import com.example.viralvideo.ui.screens.profile.ProfileViewModel
import com.example.viralvideo.ui.screens.search.SearchScreen
import com.example.viralvideo.ui.screens.search.SearchViewModel
import com.example.viralvideo.ui.screens.shorts.ShortsScreen
import com.example.viralvideo.ui.screens.shorts.ShortsViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Shorts : Screen("shorts", "Shorts", Icons.Default.FlashOn)
    object CreatorStudio : Screen("creator_studio", "Studio", Icons.Default.MonetizationOn)
    object AdminPanel : Screen("admin_panel", "Admin", Icons.Default.AdminPanelSettings)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object VideoPlayer : Screen("video_player/{videoId}", "Player", Icons.Default.PlayArrow) {
        fun createRoute(videoId: String) = "video_player/$videoId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Database & Repositories Singleton Factories
    val database = remember { AppDatabase.getDatabase(context) }
    val videoRepo = remember { VideoRepository(database.videoDao()) }
    val creatorRepo = remember { CreatorRepository(database.creatorDao()) }
    val userRepo = remember { UserRepository(database.userAccountDao()) }
    val commentRepo = remember { CommentRepository(database.commentDao()) }
    val adRepo = remember { AdRepository(database.adConfigDao()) }
    val withdrawRepo = remember { WithdrawRepository(database.withdrawDao()) }
    val watchHistoryRepo = remember { WatchHistoryRepository(database.watchHistoryDao()) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide BottomBar on VideoPlayer or Shorts screens for maximum immersive viewing
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.CreatorStudio.route,
        Screen.AdminPanel.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0x14FFFFFF), androidx.compose.ui.graphics.RectangleShape),
                    color = com.example.ui.theme.FrostedNavBg
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        tonalElevation = 0.dp
                    ) {
                        val bottomScreens = listOf(
                            Screen.Home,
                            Screen.Shorts,
                            Screen.CreatorStudio,
                            Screen.AdminPanel,
                            Screen.Profile
                        )

                        bottomScreens.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ViralRed,
                                    selectedTextColor = ViralRed,
                                    unselectedIconColor = Color.White.copy(alpha = 0.5f),
                                    unselectedTextColor = Color.White.copy(alpha = 0.5f),
                                    indicatorColor = com.example.ui.theme.GlassRedOverlay
                                ),
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title
                                    )
                                },
                                modifier = Modifier.testTag("nav_item_${screen.route}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home Route
            composable(Screen.Home.route) {
                val viewModel = viewModel<HomeViewModel> {
                    HomeViewModel(videoRepo, creatorRepo, adRepo)
                }
                val uiState by viewModel.uiState.collectAsState()

                HomeScreen(
                    uiState = uiState,
                    onCategorySelected = viewModel::onCategorySelected,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    onVideoClick = { videoId ->
                        navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                    },
                    onShortClick = { shortId ->
                        navController.navigate(Screen.Shorts.route)
                    },
                    onSubscribeToggle = viewModel::toggleCreatorSubscribe,
                    onNavigateToUpload = {
                        navController.navigate(Screen.CreatorStudio.route)
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route)
                    },
                    onNavigateToCreatorStudio = {
                        navController.navigate(Screen.CreatorStudio.route)
                    }
                )
            }

            // Video Player Route
            composable(
                route = Screen.VideoPlayer.route,
                arguments = listOf(navArgument("videoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val videoId = backStackEntry.arguments?.getString("videoId") ?: "v1"
                val viewModel = viewModel<PlayerViewModel>(key = videoId) {
                    PlayerViewModel(videoId, videoRepo, commentRepo, watchHistoryRepo)
                }
                val uiState by viewModel.uiState.collectAsState()

                VideoPlayerScreen(
                    uiState = uiState,
                    onBackClick = { navController.popBackStack() },
                    onTogglePlayPause = viewModel::togglePlayPause,
                    onSeekTo = viewModel::seekTo,
                    onSetSpeed = viewModel::setSpeed,
                    onSetQuality = viewModel::setQuality,
                    onToggleSubtitles = viewModel::toggleSubtitles,
                    onToggleTheaterMode = viewModel::toggleTheaterMode,
                    onTogglePiP = viewModel::togglePiP,
                    onToggleLike = viewModel::toggleLike,
                    onToggleWatchLater = viewModel::toggleWatchLater,
                    onCommentInputChanged = viewModel::onCommentInputChanged,
                    onSubmitComment = viewModel::submitComment,
                    onLikeComment = viewModel::likeComment,
                    onRelatedVideoClick = { relatedId ->
                        navController.navigate(Screen.VideoPlayer.createRoute(relatedId)) {
                            popUpTo(Screen.VideoPlayer.route) { inclusive = true }
                        }
                    }
                )
            }

            // Shorts Reel Route
            composable(Screen.Shorts.route) {
                val viewModel = viewModel<ShortsViewModel> {
                    ShortsViewModel(videoRepo)
                }
                val uiState by viewModel.uiState.collectAsState()

                ShortsScreen(
                    uiState = uiState,
                    onNextShort = viewModel::nextShort,
                    onPrevShort = viewModel::prevShort,
                    onTogglePlayPause = viewModel::togglePlayPause,
                    onToggleLike = viewModel::toggleLike,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Creator Studio & Monetization Route
            composable(Screen.CreatorStudio.route) {
                val viewModel = viewModel<CreatorStudioViewModel> {
                    CreatorStudioViewModel(userRepo, videoRepo, withdrawRepo)
                }
                val uiState by viewModel.uiState.collectAsState()

                CreatorStudioScreen(
                    uiState = uiState,
                    onBackClick = { navController.popBackStack() },
                    onOpenUploadDialog = viewModel::openUploadDialog,
                    onCloseUploadDialog = viewModel::closeUploadDialog,
                    onOpenWithdrawDialog = viewModel::openWithdrawDialog,
                    onCloseWithdrawDialog = viewModel::closeWithdrawDialog,
                    onTitleChanged = viewModel::onTitleChanged,
                    onDescriptionChanged = viewModel::onDescriptionChanged,
                    onCategoryChanged = viewModel::onCategoryChanged,
                    onTagsChanged = viewModel::onTagsChanged,
                    onWithdrawAmountChanged = viewModel::onWithdrawAmountChanged,
                    onWithdrawMethodChanged = viewModel::onWithdrawMethodChanged,
                    onWithdrawAccountChanged = viewModel::onWithdrawAccountChanged,
                    onSubmitUpload = viewModel::submitUploadVideo,
                    onSubmitWithdraw = viewModel::submitWithdrawRequest,
                    onDeleteVideo = viewModel::deleteVideo
                )
            }

            // Admin Panel Route
            composable(Screen.AdminPanel.route) {
                val viewModel = viewModel<AdminPanelViewModel> {
                    AdminPanelViewModel(adRepo, withdrawRepo, videoRepo)
                }
                val uiState by viewModel.uiState.collectAsState()

                AdminPanelScreen(
                    uiState = uiState,
                    onBackClick = { navController.popBackStack() },
                    onToggleAdStatus = viewModel::toggleAdStatus,
                    onApproveWithdraw = viewModel::approveWithdraw,
                    onRejectWithdraw = viewModel::rejectWithdraw,
                    onOpenNewAdDialog = viewModel::openNewAdDialog,
                    onCloseNewAdDialog = viewModel::closeNewAdDialog,
                    onNewAdTitleChanged = viewModel::onNewAdTitleChanged,
                    onNewAdFormatChanged = viewModel::onNewAdFormatChanged,
                    onNewAdPositionChanged = viewModel::onNewAdPositionChanged,
                    onSubmitCreateAd = viewModel::submitCreateAd,
                    onDeleteVideo = viewModel::deleteVideo
                )
            }

            // Profile Route
            composable(Screen.Profile.route) {
                val viewModel = viewModel<ProfileViewModel> {
                    ProfileViewModel(userRepo, watchHistoryRepo)
                }
                val uiState by viewModel.uiState.collectAsState()

                ProfileScreen(
                    uiState = uiState,
                    onBackClick = { navController.popBackStack() },
                    onToggleDarkMode = viewModel::toggleDarkMode,
                    onOpenEditProfile = viewModel::openEditProfile,
                    onCloseEditProfile = viewModel::closeEditProfile,
                    onNameChanged = viewModel::onNameChanged,
                    onBioChanged = viewModel::onBioChanged,
                    onSaveProfile = viewModel::saveProfile,
                    onToggle2FA = viewModel::toggle2FA,
                    onClearHistory = viewModel::clearWatchHistory,
                    onNavigateToStudio = { navController.navigate(Screen.CreatorStudio.route) },
                    onNavigateToAdmin = { navController.navigate(Screen.AdminPanel.route) },
                    onVideoClick = { videoId ->
                        navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                    }
                )
            }

            // Search Route
            composable(Screen.Search.route) {
                val viewModel = viewModel<SearchViewModel> {
                    SearchViewModel(videoRepo)
                }
                val uiState by viewModel.uiState.collectAsState()

                SearchScreen(
                    uiState = uiState,
                    onQueryChanged = viewModel::onQueryChanged,
                    onVideoClick = { videoId ->
                        navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
