package com.sqz.gadget.ui.layout.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.sqz.gadget.R
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
    LazyColumn {
        DashboardItem.Category.entries.forEach { category ->
            when (category) {
                DashboardItem.Category.Calculate -> CategoryText("Calculate & Conversion")
                DashboardItem.Category.Dev -> CategoryText("Dev-Related Tools")
            }
            items(category.item) {
                ClickCard(
                    title = it.title,
                    icon = it.icon,
                    onClick = { navViewModel.navigate(it.navGoal) },
                    modifier = Modifier.padding(16.dp)
                )
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

@Composable
private fun ClickCard(
    title: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
) = BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
    val widthBasedCap = maxWidth * 0.1f
    val iconMax = min(widthBasedCap, 50.dp)
    @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
    OutlinedCard(
        modifier = Modifier
            .defaultMinSize(minHeight = 100.dp)
            .height(IntrinsicSize.Min),
        colors = CardDefaults.outlinedCardColors(),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                Text(
                    text = title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                if (description != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(Modifier.fillMaxHeight(1f), verticalArrangement = Arrangement.Center) {
                val requiredSizeInModifier = Modifier.requiredSizeIn(
                    minWidth = 28.dp, minHeight = 28.dp,
                    maxWidth = iconMax, maxHeight = iconMax
                )
                Icon(
                    painter = painterResource(icon),
                    modifier = requiredSizeInModifier
                        .aspectRatio(1f, matchHeightConstraintsFirst = true),
                    contentDescription = title,
                )
            }
        }
    }
}
