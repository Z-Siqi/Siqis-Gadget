package com.sqz.gadget.ui.layout.dashboard

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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.sqz.gadget.ui.NavRoute
import gadget.desktop.generated.resources.Res
import gadget.desktop.generated.resources.circle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

class DashboardWindowList {
    data class Item(val title: String, val content: @Composable (title: String) -> Unit) {
        @Composable
        fun Content() = content(title)
    }

    @Suppress("SameParameterValue")
    private fun circleItem(
        onClick: (NavRoute) -> Unit, mode: ShapeMode
    ) = Item(title = "Calculate The Circle") {
        ItemCard(
            title = it,
            icon = Res.drawable.circle,
            onClick = { onClick(NavRoute.CircleUnit) },
            mode = mode
        )
    }

    fun calculateList(onClick: (NavRoute) -> Unit): List<Item> {
        return listOf(
            this.circleItem(onClick = onClick, mode = ShapeMode.All)
        )
    }
}

private enum class ShapeMode {
    All, Center, Top, Bottom
}

@Composable
private fun ItemCard(
    title: String,
    onClick: () -> Unit,
    icon: DrawableResource,
    modifier: Modifier = Modifier,
    mode: ShapeMode = ShapeMode.All,
) {
    val shape = when (mode) {
        ShapeMode.All -> RoundedCornerShape(22.dp)
        ShapeMode.Top -> RoundedCornerShape(22.dp, 22.dp, 5.dp, 5.dp)
        ShapeMode.Bottom -> RoundedCornerShape(5.dp, 5.dp, 22.dp, 22.dp)
        ShapeMode.Center -> RoundedCornerShape(5.dp)
    }
    val lDp = 16.dp
    val sDp = (2.5).dp
    val paddingModifier = when (mode) {
        ShapeMode.All -> Modifier.padding(lDp)
        ShapeMode.Top -> Modifier.padding(start = lDp, end = lDp, top = lDp, bottom = sDp)
        ShapeMode.Bottom -> Modifier.padding(start = lDp, end = lDp, top = sDp, bottom = lDp)
        ShapeMode.Center -> Modifier.padding(start = lDp, end = lDp, top = sDp, bottom = sDp)
    }
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val widthBasedCap = maxWidth * 0.072f
        val iconMax = min(widthBasedCap, 50.dp)
        Card(
            modifier = paddingModifier
                .defaultMinSize(minHeight = 100.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            shape = shape,
            onClick = onClick
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(Modifier.fillMaxHeight(1f), verticalArrangement = Arrangement.Center) {
                    Icon(
                        painter = painterResource(icon),
                        modifier = Modifier
                            .sizeIn(28.dp, 28.dp, 50.dp, iconMax)
                            .aspectRatio(1f, matchHeightConstraintsFirst = true),
                        contentDescription = title,
                    )
                }
            }
        }
    }
}
