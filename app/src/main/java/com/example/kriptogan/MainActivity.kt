package com.example.kriptogan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kriptogan.data.RepositoryProvider
import com.example.kriptogan.ui.navigation.BottomNavigationBar
import com.example.kriptogan.ui.navigation.NavRoutes
import com.example.kriptogan.ui.navigation.NavigationGraph
import com.example.kriptogan.ui.theme.WorkClockMailerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize repositories
        RepositoryProvider.initialize(this)
        
        // Initialize default settings and email config
        lifecycleScope.launch {
            RepositoryProvider.getSettingsRepository().initializeDefaultSettings()
            RepositoryProvider.getEmailConfigRepository().initializeDefaultConfig()
        }
        
        enableEdgeToEdge()
        setContent {
            WorkClockMailerTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                androidx.compose.material3.Scaffold(
                    bottomBar = {
                        // Only show bottom nav on main screens
                        if (currentRoute in listOf(
                            NavRoutes.CALENDAR,
                            NavRoutes.HISTORY,
                            NavRoutes.EMAIL_CONFIG,
                            NavRoutes.SETTINGS
                        )) {
                            BottomNavigationBar(
                                navController = navController,
                                currentRoute = currentRoute
                            )
                        }
                    }
                ) { paddingValues ->
                    NavigationGraph(
                        navController = navController
                    )
                }
            }
        }
    }
}
