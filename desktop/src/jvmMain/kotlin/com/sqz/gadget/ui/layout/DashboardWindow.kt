package com.sqz.gadget.ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import com.sqz.gadget.ui.common.ContentLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardWindow() {
    ContentLayout(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Siqi's Gadget") },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(
                            topEnd = 0.dp, topStart = 0.dp, bottomEnd = 0.dp, bottomStart = 12.dp
                        )
                    ),
                colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
            )
        }
    ) {
        Column(Modifier.padding(16.dp)) { //TODO
            Text("N/A")
            Text("Developing...")
        }
    }
}
