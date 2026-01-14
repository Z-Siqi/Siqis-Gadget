package com.sqz.gadget.ui.layout.info

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.gadget.common.AndroidScreenInfo
import com.sqz.gadget.ui.common.bars.BackButtonTopAppBar
import com.sqz.gadget.ui.common.bars.verticalScrollWithFixedScrollBar
import com.sqz.gadget.viewmodel.NavViewModel

@Composable
fun ScreenLayout(navViewModel: NavViewModel, screenInfo: AndroidScreenInfo, modifier: Modifier) {
    val focusManager = LocalFocusManager.current
    BackButtonTopAppBar(
        title = "Screen Information",
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { focusManager.clearFocus() }
        },
        onBackClick = { navViewModel.requestBack() },
    ) {
        SelectionContainer {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InfoCard(screenInfo = screenInfo)
            }
        }
    }
}

private fun Double.getEdgeLimitInt(): Int {
    val it = this.toInt()
    return when {
        it < 4 -> 4
        it > 70 -> 70
        else -> it
    }
}

@Composable
private fun InfoCard(screenInfo: AndroidScreenInfo) {
    val data: Map<String, Any?> = mapOf(
        "Screen Width in Dp: " to screenInfo.primaryScreenSize().first,
        "Screen Height in Dp: " to screenInfo.primaryScreenSize().second,
        "Dp to Pixel formula: dp * density (scale)" to null,
        "Screen Width in Pixel: " to screenInfo.screenPxSize().first,
        "Screen Height in Pixel: " to screenInfo.screenPxSize().second,
        "Density (screen scale factor): " to screenInfo.screenScale(),
        "Night mode support: " to screenInfo.nightModeSupport(),
        "Currently in night mode: " to screenInfo.curNightMode(),
        "Currently configured night mode: " to screenInfo.nightModeType(),
        "Current running mode type: " to screenInfo.modeType(),
        "This view is focused: " to screenInfo.isFocused(),
        "The current view is being focused by an accessibility service: " to screenInfo.isAccessibilityFocused(),
        "Screen wide color gamut support: " to screenInfo.isScreenWideColorGamut(),
        "Current scaling factor for fonts: " to screenInfo.fontScale(),
        "Number of displays connected to the device: " to screenInfo.numberOfDisplay(),
    )
    BoxWithConstraints {
        val vSideEdge = (maxHeight.value * 0.11).getEdgeLimitInt().dp
        val hSideEdge = (maxWidth.value * 0.11).getEdgeLimitInt().dp
        OutlinedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = hSideEdge, end = hSideEdge, bottom = vSideEdge)
                .padding(top = (vSideEdge / 1.618f))
        ) {
            FlowRow(
                modifier = Modifier
                    .verticalScrollWithFixedScrollBar()
                    .padding(8.dp)
            ) {
                data.entries.forEachIndexed { index, (description, value) ->
                    if (index != 0) {
                        HorizontalDivider(modifier = Modifier.padding(4.dp))
                    }
                    Text(text = description)
                    if (value != null) Text(
                        text = value.toString(),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val v = LocalView.current
    ScreenLayout(
        navViewModel = viewModel(),
        screenInfo = AndroidScreenInfo(v, v.context),
        modifier = Modifier
    )
}
