package com.sqz.gadget.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
@ReadOnlyComposable
fun isLandscape(): Boolean {
    val containerSize = LocalWindowInfo.current.containerSize
    return containerSize.height > (containerSize.width * 1.1)
}

@Composable
@ReadOnlyComposable
fun Int.pxToDpInt(): Int {
    val px = this
    val dp = with(LocalDensity.current) { px.toDp() }
    return dp.value.toInt()
}
