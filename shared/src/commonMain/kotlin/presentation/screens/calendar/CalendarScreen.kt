package presentation.screens.calendar

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import domain.model.AgendaEvent
import domain.model.AgendaMonthYear
import domain.model.AppThemeMode
import kotlinx.datetime.LocalDate
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import platform.CalendarPermissionHandler
import presentation.components.AppAlertDialog
import presentation.components.CalendarToolbarMenu
import presentation.components.ComposableLifecycle
import presentation.components.FloatingBottomNavigation
import presentation.components.NotificationPermissionDialog
import presentation.components.SafeTransparentNavigationContainer
import presentation.components.SimpleTransparentToolbar
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
    onAddEvent: (LocalDate?) -> Unit,
    onEditEvent: (AgendaEvent) -> Unit,
    onOpenDeviceSettings: () -> Unit,
    forceEventLoad: Int,
) {
    val viewModel: CalendarViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(
        pageCount = { Section.entries.size },
        initialPage = Section.Day.position,
    )
    val scope = rememberCoroutineScope()

    var scrollToTodayTrigger by remember { mutableStateOf(0) }
    var scrollToMonthTrigger by remember { mutableStateOf(0) }
    var scrollToYearTrigger by remember { mutableStateOf(0) }

    var dayScrollPosition by remember { mutableStateOf(-1L) }
    var yearScrollPosition by remember { mutableStateOf(-1) }
    var monthScrollPosition by remember { mutableStateOf<AgendaMonthYear?>(null) }

    var shouldShowNotificationPermission by remember { mutableStateOf(false) }
    
    // Calendar permission dialog state
    var showCalendarPermissionDialog by remember { mutableStateOf(false) }
    
    // Three-dot menu state
    var showToolbarMenu by remember { mutableStateOf(false) }
    
    // Settings bottom sheet state
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSettingsBottomSheet by remember { mutableStateOf(false) }
    
    // Check permission when settings sheet is dismissed (user might have granted permission)
    LaunchedEffect(settingsSheetState.isVisible) {
        if (!settingsSheetState.isVisible && showSettingsBottomSheet) {
            // Sheet was just hidden, check if permission changed
            viewModel.setEvent(Event.RefreshPermissions)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                Effect.ScrollDayToToday -> scrollToTodayTrigger++
                Effect.ScrollMonthToToday -> scrollToMonthTrigger++
                Effect.ScrollYearToToday -> scrollToYearTrigger++
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

                is Effect.ShowEdit -> onEditEvent.invoke(it.agendaEvent)
                Effect.ShowSettings -> showSettingsBottomSheet = true
            }
        }
    }

    ComposableLifecycle {
        when (it) {
            Lifecycle.Event.ON_START -> {
                viewModel.setEvent(Event.InitialDataLoad)
            }
            else -> {}
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
                            onAdd = { onAddEvent(null) },
                            onSettings = {
                                viewModel.setEvent(Event.ShowSettings)
                            },
                        )
                    }
                }
            },
            bottomBar = {
                FloatingBottomNavigation(
                    currentSection = Section.fromInt(pagerState.currentPage),
                    onSectionSelected = { tappedSection, currentSection ->
                        if (tappedSection == currentSection) {
                            viewModel.setEvent(Event.OnNowClicked(currentSection))
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(tappedSection.position)
                            }
                        }
                    }
                )
            }) {

            if (shouldShowNotificationPermission) {
                NotificationPermissionDialog(onDismissRequest = {
                    shouldShowNotificationPermission = false
                })
            }

            SafeTransparentNavigationContainer {
              if (state.noCalendars) {
                NoCalendarsView(
                    appThemeMode = appThemeMode,
                    onCreateCalendar = { viewModel.setEvent(Event.OnCreateLocalCalendarClicked) }
                )
              } else {
                HorizontalPager(state = pagerState, beyondViewportPageCount = 3) { page ->
                    when (page) {
                        Section.Day.position -> DayScreen(
                            appThemeMode = appThemeMode,
                            items = state.flatDaysAndEvents,
                            currentDate = state.currentDate,
                            scrollToNow = state.scrollToNow,
                            onNowClicked = scrollToTodayTrigger,
                            scrollToDay = dayScrollPosition,
                            showAll = state.showAll,
                            onNotify = { viewModel.setEvent(Event.Notify(it)) },
                            onHide = { viewModel.setEvent(Event.ToggleEventVisibility(it)) },
                            onEdit = { viewModel.setEvent(Event.OnEditEvent(it)) },
                            onAddEvent = onAddEvent,
                            onDetails = { /* Details handled internally in DayScreen */ },
                            onOpenSettings = { viewModel.setEvent(Event.ShowSettings) },
                            isDemoMode = state.isDemoMode,
                        )

                        Section.Month.position -> MonthsScreen(appThemeMode = appThemeMode,
                            days = state.days,
                            events = state.agendaEvents,
                            currentDate = state.currentDate,
                            onNowClicked = scrollToMonthTrigger,
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
                            onNowClicked = scrollToYearTrigger,
                            scrollToYear = yearScrollPosition
                        )
                    }
                }
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
                        onDismiss = {
                            scope.launch { settingsSheetState.hide() }.invokeOnCompletion {
                                if (!settingsSheetState.isVisible) {
                                    showSettingsBottomSheet = false
                                    viewModel.setEvent(Event.RefreshPermissions)
                                }
                            }
                        },
                        onOpenDeviceSettings = {
                            scope.launch { settingsSheetState.hide() }.invokeOnCompletion {
                                if (!settingsSheetState.isVisible) {
                                    showSettingsBottomSheet = false
                                }
                            }
                            onOpenDeviceSettings()
                        }
                    )
                }
            }
        }
    }
}