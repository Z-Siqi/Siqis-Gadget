package com.sqz.gadget.ui.layout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun ScreenLayout(modifier: Modifier = Modifier) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHigh = LocalConfiguration.current.screenHeightDp
    val smallestScreenWidthDp = LocalConfiguration.current.smallestScreenWidthDp
    val screenWidthToPx = (screenWidth * LocalDensity.current.density).toInt()
    val screenHighToPx = (screenHigh * LocalDensity.current.density).toInt()
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = modifier
                    .fillMaxSize()
                    .padding(50.dp, 100.dp, 50.dp, 100.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                val line = modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.secondary)
                Column(
                    modifier = modifier.padding(16.dp)
                ) {
                    Text(text = "Screen Width in Dp: $screenWidth")
                    Spacer(modifier = line)
                    Text(text = "Screen Height in Dp: $screenHigh")
                    Spacer(modifier = line)
                    Text(text = "Smallest Screen Width in Dp: $smallestScreenWidthDp")
                    Spacer(modifier = line)
                    Text(text = "Screen Width to Pixel (dp * density): $screenWidthToPx")
                    Spacer(modifier = line)
                    Text(text = "Screen Height to Pixel (dp * density): $screenHighToPx")
                }
            }
        }
    }
}