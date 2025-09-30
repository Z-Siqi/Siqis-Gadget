package com.sqz.gadget.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.awt.ComposeWindow
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.gadget.runtime.NavManager
import com.sqz.gadget.ui.common.ContentLayout
import com.sqz.gadget.ui.common.nav.rail.NavigationRail
import com.sqz.gadget.ui.layout.calculate.CircleUnitWindow
import com.sqz.gadget.ui.layout.dashboard.DashboardWindow
import gadget.desktop.generated.resources.Res
import gadget.desktop.generated.resources.back
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class NavRoute {
    Dashboard,
    CircleUnit, LengthUnit, HormoneUnit,
    ScreenInfo, SystemInfo
}

@Composable
@Preview
fun App(window: ComposeWindow) {
    ContentLayout(
        navigationRailLeft = {
            NavigationRail(
                extendedItem = {
                },
                foldedItem = {
                    IconButton(
                        onClick = { NavManager.current().requestBack() },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        NavHost(
            navController = NavManager.navController(
                navController = rememberNavController(),
                state = NavManager.state.collectAsState()
            ),
            startDestination = nav(NavRoute.Dashboard)
        ) {
            composable(nav(NavRoute.Dashboard)) {
                DashboardWindow()
            }
            composable(nav(NavRoute.CircleUnit)) {
                CircleUnitWindow()
            }
        }
    }
}

private fun nav(route: NavRoute): String {
    return route.name
}
