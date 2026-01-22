package com.example.kriptogan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kriptogan.data.RepositoryProvider
import com.example.kriptogan.ui.navigation.BottomNavigationBar
import com.example.kriptogan.ui.navigation.NavRoutes
import com.example.kriptogan.ui.navigation.NavigationGraph
import com.example.kriptogan.ui.theme.WorkClockMailerTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var signInResultCallback: ((GoogleSignInAccount?) -> Unit)? = null

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            signInResultCallback?.invoke(account)
        } catch (e: ApiException) {
            signInResultCallback?.invoke(null)
        }
    }

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
                // Force LTR layout direction
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
                        navController = navController,
                        onSignInRequest = { callback ->
                            signInResultCallback = callback
                            val signInIntent = RepositoryProvider.getGmailAuthService().getSignInIntent()
                            signInLauncher.launch(signInIntent)
                        }
                    )
                }
            }
        }
        }
    }
}
