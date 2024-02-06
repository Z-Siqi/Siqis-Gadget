package com.sqz.gadget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.gadget.ui.AppLayout
import com.sqz.gadget.ui.layout.calculate.CalculateLayout
import com.sqz.gadget.ui.layout.ScreenLayout
import com.sqz.gadget.ui.layout.TypingLayout
import com.sqz.gadget.ui.layout.calculate.ValueState
import com.sqz.gadget.ui.theme.SiqisGadgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val valueState: ValueState = viewModel()
            SiqisGadgetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "AppLayout"
                    ) {
                        composable("AppLayout") {
                            AppLayout(valueState, navController)
                        }
                        composable("ScreenLayout") {
                            ScreenLayout(navController)
                        }
                        composable("TypingLayout") {
                            TypingLayout(navController)
                        }
                        composable("CalculateLayout") {
                            CalculateLayout(valueState, navController)
                        }
                    }
                }
            }
        }
    }
}