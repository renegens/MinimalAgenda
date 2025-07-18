package presentation.screens.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.Lifecycle
import domain.model.AgendaEvent
import domain.model.AgendaMonthYear
import domain.model.AppThemeMode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import presentation.components.CalendarToolbarMenu
import presentation.components.ComposableLifecycle
import presentation.components.FloatingBottomNavigation
import presentation.components.HideToolTip
import presentation.components.NotificationPermissionDialog
import presentation.components.SafeTransparentNavigationContainer
import presentation.components.SimpleTransparentToolbar
import presentation.components.addHapticFeedbackProminent
import presentation.screens.calendar.CalendarContract.Effect
import presentation.screens.calendar.CalendarContract.Event
import presentation.screens.calendar.CalendarContract.Section
import presentation.screens.calendar.day.DayScreen
import presentation.screens.calendar.months.MonthsScreen
import presentation.screens.calendar.years.YearsScreen
import presentation.screens.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    appThemeMode: AppThemeMode,
    onAddEvent: () -> Unit,
    onEditEvent: (AgendaEvent) -> Unit,
    notEligible: () -> Unit,
    onOpenDeviceSettings: () -> Unit,
    forceEventLoad: Int,
    isPresentingNotificationOnboarding: Boolean
) {
    val viewModel: CalendarViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(
        pageCount = { Section.entries.size },
        initialPage = Section.Day.position,
    )
    val tipSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showTipSheet by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    var isScrolledToToday by remember { mutableStateOf(true) }
    var isScrolledToMonth by remember { mutableStateOf(true) }
    var isScrolledToYear by remember { mutableStateOf(true) }

    var dayScrollPosition by remember { mutableStateOf(-1) }
    var yearScrollPosition by remember { mutableStateOf(-1) }
    var monthScrollPosition by remember { mutableStateOf<AgendaMonthYear?>(null) }

    var shouldShowNotificationPermission by remember { mutableStateOf(false) }
    
    // Three-dot menu state
    var showToolbarMenu by remember { mutableStateOf(false) }
    
    // Settings bottom sheet state
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSettingsBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            haptic.addHapticFeedbackProminent()
            // Settings is now in a bottom sheet, so we don't need this check
            if (page == Section.Day.position){
                viewModel.setEvent(Event.ShowBottomSheet)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                Effect.ScrollDayToToday -> isScrolledToToday = !isScrolledToToday
                Effect.ScrollMonthToToday -> isScrolledToMonth = !isScrolledToMonth
                Effect.ScrollYearToToday -> isScrolledToYear = !isScrolledToYear
                is Effect.GoToDay -> {
                    pagerState.animateScrollToPage(page = Section.Day.position)
                    dayScrollPosition = it.epochDay
                }

                is Effect.GoToMonth -> {
                    pagerState.animateScrollToPage(page = Section.Month.position)
                    monthScrollPosition = it.agendaMonthYear
                }

                is Effect.GoToYear -> {
                    pagerState.animateScrollToPage(page = Section.Year.position)
                    yearScrollPosition = it.agendaYear.year
                }

                is Effect.NotificationPermissionMissing -> {
                    shouldShowNotificationPermission = true
                }

                is Effect.ShowPaywall -> notEligible.invoke()
                is Effect.ShowEdit -> onEditEvent.invoke(it.agendaEvent)
                Effect.ShowSheetTip -> showTipSheet = true
                Effect.DismissSheet -> scope.launch { tipSheetState.hide() }.invokeOnCompletion {
                    if (!tipSheetState.isVisible) {
                        showTipSheet = false
                    }
                }
                Effect.ShowSettings -> showSettingsBottomSheet = true
            }
        }
    }

    ComposableLifecycle {
        if (it == Lifecycle.Event.ON_START) {
            viewModel.setEvent(Event.InitialDataLoad)
        }
    }

    LaunchedEffect(forceEventLoad) {
        if (forceEventLoad > 0) {
            viewModel.setEvent(Event.ForceReload)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(containerColor = Color.Transparent,
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                SimpleTransparentToolbar(
                    backgroundColor = Color.Transparent,
                    onMenuClick = { showToolbarMenu = true }
                ) {
                    // Empty content - no title, just the menu button will be added separately
                }
            },
            bottomBar = {
                FloatingBottomNavigation(
                    currentSection = Section.fromInt(pagerState.currentPage),
                    onSectionSelected = { section ->
                        scope.launch {
                            pagerState.animateScrollToPage(section.position)
                        }
                    }
                )
            }) {
            if (shouldShowNotificationPermission) {
                NotificationPermissionDialog(onDismissRequest = {
                    shouldShowNotificationPermission = false
                })
            }

            //SwipeToolTip(state.showSwipeToolTip)
            
            SafeTransparentNavigationContainer {
                HorizontalPager(state = pagerState, beyondViewportPageCount = 3) { page ->
                    when (page) {
                        Section.Day.position -> DayScreen(
                            appThemeMode = appThemeMode,
                            items = state.flatDaysAndEvents,
                            currentDate = state.currentDate,
                            scrollToNow = state.scrollToNow,
                            onNowClicked = isScrolledToToday,
                            scrollToDay = dayScrollPosition,
                            calendarPermission = state.calendarPermission,
                            showAll = state.showAll,
                            onNotify = { viewModel.setEvent(Event.Notify(it)) },
                            onHide = { viewModel.setEvent(Event.ToggleEventVisibility(it)) },
                            onEdit = { viewModel.setEvent(Event.OnEditEvent(it)) },
                            onRequestCalendarPermission = {
                                viewModel.setEvent(Event.OnRequestCalendarPermissionClicked)
                            },
                            isPresentingSwipeTip = state.showSwipeToolTip,
                            isPresentingNotificationOnBoarding = isPresentingNotificationOnboarding,
                            onOpenSettings = onOpenDeviceSettings,
                            isDemoMode = state.isDemoMode,
                        )

                        Section.Month.position -> MonthsScreen(appThemeMode = appThemeMode,
                            days = state.days,
                            events = state.agendaEvents,
                            currentDate = state.currentDate,
                            onNowClicked = isScrolledToMonth,
                            scrollToMonth = monthScrollPosition,
                            showAll = state.showAll,
                            onDayClicked = {
                                viewModel.setEvent(Event.OnDayClicked(epochDay = it))
                            },
                            onYearClicked = {
                                viewModel.setEvent(Event.OnYearClicked(it))
                            })

                        Section.Year.position -> YearsScreen(
                            appThemeMode = appThemeMode,
                            days = state.days,
                            events = state.agendaEvents,
                            showAll = state.showAll,
                            currentDate = state.currentDate,
                            onMonthClicked = { viewModel.setEvent(Event.OnMonthClicked(it)) },
                            onNowClicked = isScrolledToYear,
                            scrollToYear = yearScrollPosition
                        )
                    }
                }
            }
            
            if (showTipSheet) {
                ModalBottomSheet(
                    contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
                    dragHandle = {},
                    onDismissRequest = {
                        viewModel.setEvent(Event.DismissBottomSheet)
                    },
                    sheetState = tipSheetState
                ) {
                    HideToolTip(openPaywall = {
                        viewModel.setEvent(Event.ShowPaywallFromBottomSheet)
                    })
                }
            }
            
            if (showSettingsBottomSheet) {
                ModalBottomSheet(
                    containerColor = MaterialTheme.colorScheme.surface,
                    onDismissRequest = {
                        scope.launch { settingsSheetState.hide() }.invokeOnCompletion {
                            if (!settingsSheetState.isVisible) {
                                showSettingsBottomSheet = false
                            }
                        }
                    },
                    sheetState = settingsSheetState
                ) {
                    SettingsScreen(
                        notEligible = notEligible,
                        onDismiss = {
                            scope.launch { settingsSheetState.hide() }.invokeOnCompletion {
                                if (!settingsSheetState.isVisible) {
                                    showSettingsBottomSheet = false
                                }
                            }
                        }
                    )
                }
            }
        }
        
        // Three-dot menu with proper anchoring - positioned at top-right
        if (showToolbarMenu) {
            CalendarToolbarMenu(
                isVisible = showToolbarMenu,
                onDismiss = { showToolbarMenu = false },
                showAll = state.showAll,
                onNowClicked = {
                    viewModel.setEvent(
                        Event.OnNowClicked(Section.fromInt(pagerState.currentPage))
                    )
                },
                onToggleActive = {
                    viewModel.setEvent(Event.ToggleActive)
                },
                onAdd = onAddEvent,
                onSettings = {
                    viewModel.setEvent(Event.ShowSettings)
                },
            )
        }
    }
}