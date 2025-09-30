package com.sqz.gadget.ui.layout.dashboard

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.gadget.runtime.NavManager
import com.sqz.gadget.ui.NavRoute
import com.sqz.gadget.ui.common.ContentLayout
import com.sqz.gadget.ui.common.bars.LabelTopAppBar

@Composable
fun DashboardWindow() {
    ContentLayout(
        modifier = Modifier.fillMaxSize(),
        topBar = { LabelTopAppBar("Siqi's Gadget") }
    ) {
        val dashboardWindowList = DashboardWindowList()
        val scrollState = rememberScrollState()
        Card(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(scrollState),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            CategoryText("Calculate & Conversion")
            dashboardWindowList.calculateList(onClick = { requestNav(it) }).forEach {
                it.Content()
            }
            //TODO
        }
    }
}

private fun requestNav(route: NavRoute) {
    when (route) {
        NavRoute.CircleUnit -> {
            NavManager.current().navigate(NavRoute.CircleUnit)
        }

        else -> throw IllegalArgumentException("Unknown route: $route")
    }
}

@Composable
private fun CategoryText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = modifier.padding(top = 16.dp, start = 16.dp)
    )
}

@Preview
@Composable
private fun Preview() {
    DashboardWindow()
}
