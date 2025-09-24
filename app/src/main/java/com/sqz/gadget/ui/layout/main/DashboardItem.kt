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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.sqz.gadget.R
import com.sqz.gadget.ui.NavRoute

class DashboardItem {
    data class Item(val title: String, val content: @Composable (title: String) -> Unit) {
        @Composable
        fun Content() = content(title)
    }

    private fun circleItem(onClick: (NavRoute) -> Unit) = Item(title = "Calculate The Circle") {
        ClickCard(
            title = it,
            icon = R.drawable.circle,
            onClick = { onClick(NavRoute.CircleUnit) },
            modifier = Modifier.padding(16.dp)
        )
    }

    @Composable
    fun CircleItem(onClick: (NavRoute) -> Unit) {
        this.circleItem(onClick = onClick).Content()
    }

    fun list(onClick: (NavRoute) -> Unit): List<Item> {
        return listOf(
            this.circleItem(onClick = onClick)
        )
    }
}

@Composable
private fun ClickCard(
    title: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
) = BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
    val widthBasedCap = maxWidth * 0.12f
    val iconMax = min(widthBasedCap, 50.dp)
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
