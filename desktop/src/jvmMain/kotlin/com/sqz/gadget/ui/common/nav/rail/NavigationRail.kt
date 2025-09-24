package com.sqz.gadget.ui.common.nav.rail

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gadget.desktop.generated.resources.Res
import gadget.desktop.generated.resources.menu
import org.jetbrains.compose.resources.painterResource

@Composable
fun NavigationRail(
    extendedItem: @Composable ColumnScope.() -> Unit = {},
    foldedItem: @Composable ColumnScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    var extend by remember { mutableStateOf(false) }
    val width by animateDpAsState(if (extend) 220.dp else 50.dp)
    Card(
        modifier = modifier.fillMaxHeight().width(width.value.dp),
        shape = RoundedCornerShape(
            topStart = 0.dp, bottomStart = 0.dp,
            topEnd = 10.dp, bottomEnd = 10.dp
        ),
        colors = CardDefaults.cardColors()
    ) {
        IconButton(onClick = { extend = !extend }) {
            Icon(
                painter = painterResource(Res.drawable.menu),
                contentDescription = "Menu"
            )
        }
        if (extend) {
            extendedItem()
        } else {
            foldedItem()
        }
    }
}

@Preview
@Composable
private fun Preview() {
    NavigationRail()
}
