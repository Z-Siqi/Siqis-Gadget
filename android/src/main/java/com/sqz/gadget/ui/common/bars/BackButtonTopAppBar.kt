package com.sqz.gadget.ui.common.bars

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sqz.gadget.R
import com.sqz.gadget.ui.theme.SystemBarsColor

/**
 * A BackButtonTopAppBar that uses a [TopAppBar] to layout the title and actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackButtonTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painterResource(R.drawable.back),
                    stringResource(R.string.back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = TopAppBarDefaults.topAppBarColors().titleContentColor
        )
    )
}

/**
 * Screen with [Scaffold] that uses [BackButtonTopAppBar] to layout the top bar.
 */
@Composable
fun BackButtonTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    SystemBarsColor.current.setStateBarColor(MaterialTheme.colorScheme.surface)
    SystemBarsColor.current.setNavBarColor(MaterialTheme.colorScheme.surfaceContainerHighest)
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Scaffold(
            topBar = {
                BackButtonTopAppBar(
                    title = title,
                    onBackClick = onBackClick,
                    modifier = modifier
                )
            },
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.ime
        ) { innerPadding ->
            Surface(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                color = Color.Transparent,
                content = content
            )
        }
    }
}
