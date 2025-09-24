package com.sqz.gadget.ui.layout.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.gadget.R
import com.sqz.gadget.ui.NavRoute
import com.sqz.gadget.ui.common.bars.LargeTopAppBar
import com.sqz.gadget.viewmodel.NavViewModel

@Composable
fun Dashboard(
    navViewModel: NavViewModel,
    modifier: Modifier = Modifier
) = LargeTopAppBar(
    title = stringResource(R.string.app_full_name),
    backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
    modifier = modifier
) {
    val dashboardItem = DashboardItem()
    LazyColumn {
        CategoryText("Calculate & Conversion")
        item {
            dashboardItem.CircleItem {
                navViewModel.navigate(it)
            }
        }
    }
}

@Suppress("FunctionName")
private fun LazyListScope.CategoryText(text: String, modifier: Modifier = Modifier) = item {
    Text(
        text = text,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = modifier.padding(top = 16.dp, start = 18.dp)
    )
}
