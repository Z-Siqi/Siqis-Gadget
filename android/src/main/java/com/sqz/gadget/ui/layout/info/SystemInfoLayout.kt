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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.gadget.common.AndroidSystemInfo
import com.sqz.gadget.ui.common.bars.BackButtonTopAppBar
import com.sqz.gadget.ui.common.bars.verticalScrollWithFixedScrollBar
import com.sqz.gadget.viewmodel.NavViewModel

@Composable
fun SystemInfoLayout(
    navViewModel: NavViewModel,
    systemInfo: AndroidSystemInfo,
    modifier: Modifier,
) {
    val focusManager = LocalFocusManager.current
    BackButtonTopAppBar(
        title = "System Information",
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
                InfoCard(sysInfo = systemInfo)
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
private fun InfoCard(sysInfo: AndroidSystemInfo) {
    val data: Map<String, Any?> = mapOf(
        "Device name: " to sysInfo.deviceName(),
        "SDK version: " to sysInfo.sdkInt(),
        "Android version: " to sysInfo.systemVersion(),
        "Device name of industrial design: " to sysInfo.hardwareDevice(),
        "Model: " to sysInfo.deviceModel(),
        "Development codename: " to sysInfo.devCodename(),
        "Build Number (id): " to sysInfo.systemBuild(),
        "Fingerprint: " to sysInfo.fingerprint(),
        "Security patch level: " to sysInfo.securityPatch(),
        "CPU model: " to sysInfo.cpuModel(),
        "Supported ABI: " to sysInfo.abi(),
        "Machine architecture: " to sysInfo.machineArch(),
        "OS release (Kernel): " to sysInfo.osRelease(),
        "OS name: " to sysInfo.osSysName(),
        "OS version: " to sysInfo.osVersion(),
        "Product: " to sysInfo.product(),
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
                val onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
                data.entries.forEachIndexed { index, (description, value) ->
                    if (index != 0) {
                        HorizontalDivider(modifier = Modifier.padding(4.dp))
                    }
                    val text = remember(description, value) {
                        buildAnnotatedString {
                            append(description)
                            if (value != null) withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.Medium,
                                    color = onPrimaryContainer
                                )
                            ) { append(value.toString()) }
                        }
                    }
                    Text(text = text)
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SystemInfoLayout(
        navViewModel = viewModel(),
        systemInfo = AndroidSystemInfo(LocalContext.current),
        modifier = Modifier
    )
}
