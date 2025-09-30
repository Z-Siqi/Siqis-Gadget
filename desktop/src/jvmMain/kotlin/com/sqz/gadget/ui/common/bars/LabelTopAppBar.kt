package com.sqz.gadget.ui.common.bars

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )
        },
        modifier = modifier
            .padding(start = 8.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(
                    topEnd = 0.dp, topStart = 0.dp, bottomEnd = 0.dp, bottomStart = 12.dp
                )
            ),
        colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
    )
}

@Preview
@Composable
private fun Preview() {
    LabelTopAppBar("Siqi's Gadget")
}
