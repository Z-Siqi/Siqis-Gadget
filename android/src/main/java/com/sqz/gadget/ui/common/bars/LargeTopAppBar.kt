package com.sqz.gadget.ui.common.bars

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.sqz.gadget.ui.common.isLandscape
import com.sqz.gadget.ui.theme.SystemBarsColor

/**
 * A TopAppBar that uses a [LargeTopAppBar] to layout the title and actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )
) {
    val disableMove = if (!isLandscape()) Modifier else {
        Modifier.pointerInteropFilter(null) { true }
    }
    LargeTopAppBar(
        modifier = modifier then disableMove,
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            titleContentColor = MaterialTheme.colorScheme.secondary,
        ),
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
            )
        },
        scrollBehavior = scrollBehavior,
    )
    if (scrollBehavior.state.collapsedFraction > 0.38f) {
        SystemBarsColor.current.setStateBarColor(MaterialTheme.colorScheme.surfaceContainerHigh)
    } else {
        SystemBarsColor.current.setStateBarColor(MaterialTheme.colorScheme.surfaceContainerLow)
    }
    SystemBarsColor.current.setNavBarColor(MaterialTheme.colorScheme.surfaceVariant)
}

/**
 * Screen with [Scaffold] that uses [LargeTopAppBar] to layout the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable () -> Unit
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { LargeTopAppBar(title = title, scrollBehavior = scrollBehavior) },
        contentWindowInsets = WindowInsets.ime
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = backgroundColor,
            content = content
        )
    }
}
