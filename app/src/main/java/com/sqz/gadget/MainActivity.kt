package com.sqz.gadget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.gadget.ui.MainLayout
import com.sqz.gadget.ui.layout.AppLayout
import com.sqz.gadget.ui.layout.calculate.CalculateLayout
import com.sqz.gadget.ui.layout.ScreenLayout
import com.sqz.gadget.ui.layout.SystemInfoLayout
import com.sqz.gadget.ui.layout.TypingLayout
import com.sqz.gadget.ui.layout.calculate.ValueState
import com.sqz.gadget.ui.theme.SiqisGadgetTheme
import com.sqz.gadget.ui.theme.SystemBarsColor

private const val newModel: Boolean = true

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SiqisGadgetTheme {
                SystemBarsColor.CreateSystemBars(window) {
                    SystemBarsColor.current.let {
                        it.setStateBarColor(MaterialTheme.colorScheme.surfaceContainerLow)
                        it.setNavBarColor(MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
                if (isSystemInDarkTheme()) {
                    SystemBarsColor.current.setLightBars(lightState = true, lightNav = true)
                } else {
                    SystemBarsColor.current.setLightBars(lightState = false, lightNav = false)
                }

                if (!newModel) Surface(modifier = Modifier.fillMaxSize()) {
                    val valueState: ValueState = viewModel()
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "AppLayout"
                    ) {
                        composable("AppLayout") {
                            AppLayout(valueState, navController)
                            window.navigationBarColor = MaterialTheme.colorScheme.surfaceVariant.toArgb()
                        }
                        composable("ScreenLayout") {
                            ScreenLayout(navController)
                            window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()
                        }
                        composable("SystemInfoLayout") {
                            SystemInfoLayout(navController)
                            window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()
                        }
                        composable("CalculateLayout") {
                            CalculateLayout(valueState, navController)
                            window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()
                        }
                        composable("TypingLayout") {
                            TypingLayout(navController)
                            window.navigationBarColor = MaterialTheme.colorScheme.surfaceContainer.toArgb()
                        }
                    }
                }

                val notButtonNav = WindowInsets.navigationBars.getBottom(LocalDensity.current) < 100
                val windowInsetsPadding = if (!notButtonNav) // if nav mode is not gesture mode
                    Modifier.windowInsetsPadding(WindowInsets.navigationBars) else Modifier
                Surface(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars) // Do not override state bar area
                        .fillMaxSize() then windowInsetsPadding,
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainLayout(
                        context = applicationContext,
                        view = window.decorView,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}
