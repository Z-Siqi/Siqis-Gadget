package com.sqz.gadget.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ContentLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    navigationRailLeft: @Composable () -> Unit = {},
    navigationRailRight: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (paddingValues: PaddingValues) -> Unit = {}
) = Row(modifier = Modifier.background(MaterialTheme.colors.surface)) {
    Surface(color = Color.Transparent) { navigationRailLeft() }
    Surface(
        modifier = Modifier.weight(1f),
        color = Color.Transparent
    ) {
        Scaffold(
            topBar = topBar,
            bottomBar = bottomBar,
            floatingActionButton = floatingActionButton,
            containerColor = Color.Transparent
        ) { paddingValues ->
            Surface(
                modifier = modifier.padding(paddingValues),
                color = Color.Transparent
            ) {
                content(paddingValues)
            }
        }
    }
    Surface(color = Color.Transparent) { navigationRailRight() }
}
