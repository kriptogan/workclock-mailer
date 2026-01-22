package com.example.kriptogan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kriptogan.data.RepositoryProvider
import com.example.kriptogan.ui.calendar.CalendarScreen
import com.example.kriptogan.ui.calendar.CalendarViewModel
import com.example.kriptogan.ui.daydetail.DayDetailScreen
import com.example.kriptogan.ui.daydetail.DayDetailViewModel
import com.example.kriptogan.ui.emailconfig.EmailConfigScreen
import com.example.kriptogan.ui.emailconfig.EmailConfigViewModel
import com.example.kriptogan.ui.settings.SettingsScreen
import com.example.kriptogan.ui.settings.SettingsViewModel
import com.example.kriptogan.util.DateUtils
import java.time.LocalDate

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onSignInRequest: ((com.google.android.gms.auth.api.signin.GoogleSignInAccount?) -> Unit) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.CALENDAR
    ) {
        composable(NavRoutes.CALENDAR) {
            val calendarViewModel: CalendarViewModel = viewModel {
                CalendarViewModel(
                    RepositoryProvider.getWorkDayRepository(),
                    RepositoryProvider.getSettingsRepository()
                )
            }
            val settings by RepositoryProvider.getSettingsRepository().getSettingsFlow()
                .collectAsState(initial = null)
            
            CalendarScreen(
                viewModel = calendarViewModel,
                settings = settings,
                onDayClick = { date ->
                    val epochDay = DateUtils.dateToEpochDay(date)
                    navController.navigate(NavRoutes.dayDetail(epochDay))
                }
            )
        }
        
        composable(
            route = NavRoutes.CALENDAR_MONTH,
            arguments = listOf(
                navArgument("year") { type = NavType.IntType },
                navArgument("month") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val year = backStackEntry.arguments?.getInt("year") ?: java.time.Year.now().value
            val month = backStackEntry.arguments?.getInt("month") ?: java.time.MonthDay.now().monthValue
            val yearMonth = java.time.YearMonth.of(year, month)
            
            val calendarViewModel: CalendarViewModel = viewModel {
                CalendarViewModel(
                    RepositoryProvider.getWorkDayRepository(),
                    RepositoryProvider.getSettingsRepository()
                ).apply {
                    loadMonth(yearMonth)
                }
            }
            val settings by RepositoryProvider.getSettingsRepository().getSettingsFlow()
                .collectAsState(initial = null)
            
            CalendarScreen(
                viewModel = calendarViewModel,
                settings = settings,
                onDayClick = { date ->
                    val epochDay = DateUtils.dateToEpochDay(date)
                    navController.navigate(NavRoutes.dayDetail(epochDay))
                }
            )
        }
        
        composable(NavRoutes.SETTINGS) {
            val settingsViewModel: SettingsViewModel = viewModel {
                SettingsViewModel(RepositoryProvider.getSettingsRepository())
            }
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(NavRoutes.EMAIL_CONFIG) {
            val context = LocalContext.current
            val emailConfigViewModel: EmailConfigViewModel = viewModel {
                EmailConfigViewModel(
                    context.applicationContext as android.app.Application,
                    RepositoryProvider.getEmailConfigRepository(),
                    RepositoryProvider.getGmailAuthService()
                )
            }
            
            EmailConfigScreen(
                viewModel = emailConfigViewModel,
                onNavigateBack = { navController.popBackStack() },
                onSignInRequest = {
                    onSignInRequest { account ->
                        emailConfigViewModel.handleSignInResult(account)
                    }
                }
            )
        }
        
        composable(NavRoutes.HISTORY) {
            val historicalMonthsViewModel: com.example.kriptogan.ui.history.HistoricalMonthsViewModel = viewModel {
                com.example.kriptogan.ui.history.HistoricalMonthsViewModel(
                    RepositoryProvider.getWorkDayRepository(),
                    RepositoryProvider.getSettingsRepository()
                )
            }
            com.example.kriptogan.ui.history.HistoricalMonthsScreen(
                viewModel = historicalMonthsViewModel,
                onMonthClick = { yearMonth ->
                    navController.navigate(NavRoutes.calendarMonth(yearMonth.year, yearMonth.monthValue))
                }
            )
        }
        
        composable(
            route = NavRoutes.DAY_DETAIL,
            arguments = listOf(
                navArgument("date") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val dateEpochDay = backStackEntry.arguments?.getLong("date")
                ?: DateUtils.dateToEpochDay(LocalDate.now())
            val date = DateUtils.epochDayToDate(dateEpochDay)
            
            val dayDetailViewModel: DayDetailViewModel = viewModel {
                DayDetailViewModel(
                    date,
                    RepositoryProvider.getWorkDayRepository(),
                    RepositoryProvider.getSettingsRepository()
                )
            }
            DayDetailScreen(
                viewModel = dayDetailViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
