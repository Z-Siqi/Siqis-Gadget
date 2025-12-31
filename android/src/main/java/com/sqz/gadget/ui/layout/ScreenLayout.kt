package com.sqz.gadget.ui.layout

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sqz.gadget.R

@SuppressLint("SwitchIntDef")
@Composable
fun ScreenLayout(navController: NavController, modifier: Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHigh = LocalConfiguration.current.screenHeightDp
    val smallestScreenWidthDp = LocalConfiguration.current.smallestScreenWidthDp
    val screenWidthToPx = (screenWidth * LocalDensity.current.density).toInt()
    val screenHighToPx = (screenHigh * LocalDensity.current.density).toInt()
    val umm = LocalContext.current.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val currentModeType = when (umm.currentModeType) {
        1 -> "TYPE_NORMAL"
        2 -> "TYPE_DESK"
        3 -> "TYPE_CAR"
        4 -> "TYPE_TELEVISION"
        5 -> "TYPE_APPLIANCE"
        6 -> "TYPE_WATCH"
        7 -> "TYPE_VR_HEADSET"
        else -> "Unknown"
    }
    val nightMode = when (umm.nightMode) {
        -1 -> "ERROR"
        0 -> "MODE_NIGHT_AUTO"
        1 -> "MODE_NIGHT_NO"
        2 -> "MODE_NIGHT_YES"
        3 -> "MODE_NIGHT_CUSTOM"
        else -> "Unknown"
    }
    val isNightModeActive = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        LocalConfiguration.current.isNightModeActive
    } else {
        false
    }
    val isScreenWideColorGamut = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalConfiguration.current.isScreenWideColorGamut
    } else {
        "N/A: system must more than 26"
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = modifier.padding(10.dp)
            ) {
                Icon(painterResource(R.drawable.back), stringResource(R.string.back))
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = modifier
                    .fillMaxSize()
                    .padding(50.dp, 100.dp, 50.dp, 80.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                SelectionContainer {
                    Column(
                        modifier = modifier
                            .padding(16.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = "Screen Width in Dp: $screenWidth")
                        HorizontalDivider()
                        Text(text = "Screen Height in Dp: $screenHigh")
                        HorizontalDivider()
                        Text(text = "Smallest Screen Width in Dp: $smallestScreenWidthDp")
                        HorizontalDivider()
                        Text(text = "Screen Width to Pixel (dp * density): $screenWidthToPx")
                        HorizontalDivider()
                        Text(text = "Screen Height to Pixel (dp * density): $screenHighToPx")
                        HorizontalDivider()
                        Text(text = "Night mode active state: $isNightModeActive")
                        HorizontalDivider()
                        Text(text = "Current running mode type: UI_MODE_$currentModeType")
                        HorizontalDivider()
                        Text(text = "Currently configured night mode: $nightMode")
                        HorizontalDivider()
                        Text(text = "Screen wide color gamut support: $isScreenWideColorGamut")
                    }
                }
            }
        }
    }
}