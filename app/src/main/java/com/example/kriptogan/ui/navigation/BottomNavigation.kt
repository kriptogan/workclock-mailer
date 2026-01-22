package com.example.kriptogan.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendar") },
            label = { Text("Calendar") },
            selected = currentRoute == NavRoutes.CALENDAR,
            onClick = { navController.navigate(NavRoutes.CALENDAR) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "History") },
            label = { Text("History") },
            selected = currentRoute == NavRoutes.HISTORY,
            onClick = { navController.navigate(NavRoutes.HISTORY) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            label = { Text("Email") },
            selected = currentRoute == NavRoutes.EMAIL_CONFIG,
            onClick = { navController.navigate(NavRoutes.EMAIL_CONFIG) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentRoute == NavRoutes.SETTINGS,
            onClick = { navController.navigate(NavRoutes.SETTINGS) }
        )
    }
}