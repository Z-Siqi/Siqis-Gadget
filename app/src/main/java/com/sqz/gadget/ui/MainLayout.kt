package com.sqz.gadget.ui

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.gadget.ui.layout.calculate.CircleUnitLayout
import com.sqz.gadget.ui.layout.main.Dashboard
import com.sqz.gadget.viewmodel.NavViewModel

enum class NavRoute {
    Dashboard,
    CircleUnit, LengthUnit, HormoneUnit,
    ScreenInfo, SystemInfo, TypingTest
}

@Composable
fun MainLayout(context: Context, view: View, modifier: Modifier) {
    val navViewModel: NavViewModel = viewModel()
    NavHost(
        navController = navViewModel.navController(
            navController = rememberNavController(),
            state = navViewModel.state.collectAsState()
        ),
        startDestination = nav(NavRoute.Dashboard),
        modifier = modifier
    ) {
        composable(nav(NavRoute.Dashboard)) {
            Dashboard(navViewModel = navViewModel, modifier = modifier)
        }
        composable(nav(NavRoute.CircleUnit)) {
            CircleUnitLayout(navViewModel = navViewModel, modifier = modifier)
        }
        composable(nav(NavRoute.LengthUnit)) { //TODO
        }
    }
}

private fun nav(route: NavRoute): String {
    return route.name
}
