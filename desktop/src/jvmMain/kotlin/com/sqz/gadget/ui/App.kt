package com.sqz.gadget.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposeWindow
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.gadget.ui.common.ContentLayout
import com.sqz.gadget.ui.common.nav.rail.NavigationRail
import com.sqz.gadget.ui.layout.DashboardWindow
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class NavRoute {
    Dashboard,
    CircleUnit, LengthUnit, HormoneUnit,
    ScreenInfo, SystemInfo
}

@Composable
@Preview
fun App(window: ComposeWindow) {
    val navController = rememberNavController()
    var selectedDestination by rememberSaveable { mutableIntStateOf(0) }
    ContentLayout(
        navigationRailLeft = {
            NavigationRail(
                extendedItem = {
                },
                foldedItem = {
                }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = nav(NavRoute.Dashboard)
        ) {
            composable(nav(NavRoute.Dashboard)) {
                DashboardWindow()
            }
            composable(nav(NavRoute.CircleUnit)) {
                //TODO
            }
        }
    }
}

private fun nav(route: NavRoute): String {
    return route.name
}
