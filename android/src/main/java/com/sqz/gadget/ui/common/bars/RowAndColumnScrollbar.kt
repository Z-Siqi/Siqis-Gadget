package com.sqz.gadget.ui.common.bars

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.verticalColumnScrollbar(
    scrollState: ScrollState,
    width: Dp = 4.dp,
    showScrollBar: Boolean = true,
    scrollBarTrackColor: Color = Color.Gray,
    scrollBarColor: Color = Color.Black,
    scrollBarCornerRadius: Float = 4f,
    endPadding: Float = 12f,
    topBottomPadding: Float = 0f,
): Modifier {
    return if (showScrollBar) drawWithContent {
        val topBottom = if (topBottomPadding == 0f) 0f else topBottomPadding / 2
        // Draw the column's content
        drawContent()
        // Dimensions and calculations
        val viewportHeight = this.size.height - topBottomPadding
        val totalContentHeight = scrollState.maxValue.toFloat() + viewportHeight
        val scrollValue = scrollState.value.toFloat()
        // Compute scrollbar height and position
        val scrollBarHeight =
            (viewportHeight / totalContentHeight) * viewportHeight
        val scrollBarStartOffset =
            (scrollValue / totalContentHeight) * viewportHeight
        // Draw the track (disable: set scrollBarTrackColor to Color.Transparent)
        drawRoundRect(
            cornerRadius = CornerRadius(scrollBarCornerRadius),
            color = scrollBarTrackColor,
            topLeft = Offset(this.size.width - endPadding, topBottom),
            size = Size(width.toPx(), viewportHeight),
        )
        // Draw the scrollbar
        drawRoundRect(
            cornerRadius = CornerRadius(scrollBarCornerRadius),
            color = scrollBarColor,
            topLeft = Offset(this.size.width - endPadding, scrollBarStartOffset + topBottom),
            size = Size(width.toPx(), scrollBarHeight)
        )
    } else drawWithContent {
        drawContent()
    }
}

@Composable
fun Modifier.verticalScrollWithFixedScrollBar(): Modifier {
    val scrollState = rememberScrollState()
    val verticalColumnScrollbar = this.verticalColumnScrollbar(
        scrollState = scrollState,
        showScrollBar = scrollState.canScrollBackward || scrollState.canScrollForward,
        scrollBarTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        scrollBarColor = MaterialTheme.colorScheme.secondary,
    )
    return verticalColumnScrollbar.verticalScroll(scrollState)
}

@Composable
fun Modifier.horizontalRowScrollbar(
    scrollState: ScrollState,
    height: Dp = 4.dp,
    showScrollBar: Boolean = true,
    scrollBarTrackColor: Color = Color.Gray,
    scrollBarColor: Color = Color.Black,
    scrollBarCornerRadius: Float = 4f,
    endPadding: Float = 12f,
    startEndPadding: Float = 0f,
): Modifier {
    return if (showScrollBar) drawWithContent {
        val startEnd = if (startEndPadding == 0f) 0f else startEndPadding / 2
        // Draw the row's content
        drawContent()
        // Dimensions and calculations
        val viewportWidth = this.size.width - startEndPadding
        val totalContentWidth = scrollState.maxValue.toFloat() + viewportWidth
        val scrollValue = scrollState.value.toFloat()
        // Compute scrollbar width and position
        val scrollBarWidth =
            (viewportWidth / totalContentWidth) * viewportWidth
        val scrollBarStartOffset =
            (scrollValue / totalContentWidth) * viewportWidth
        // Draw the track (disable: set scrollBarTrackColor to Color.Transparent)
        drawRoundRect(
            cornerRadius = CornerRadius(scrollBarCornerRadius),
            color = scrollBarTrackColor,
            topLeft = Offset(startEnd, this.size.height - endPadding),
            size = Size(viewportWidth, height.toPx()),
        )
        // Draw the scrollbar
        drawRoundRect(
            cornerRadius = CornerRadius(scrollBarCornerRadius),
            color = scrollBarColor,
            topLeft = Offset(scrollBarStartOffset + startEnd, this.size.height - endPadding),
            size = Size(scrollBarWidth, height.toPx())
        )
    } else drawWithContent {
        drawContent()
    }
}

@Composable
fun Modifier.horizontalScrollWithFixedScrollBar(): Modifier {
    val scrollState = rememberScrollState()
    val horizontalColumnScrollbar = this.horizontalRowScrollbar(
        scrollState = scrollState,
        showScrollBar = scrollState.canScrollBackward || scrollState.canScrollForward,
        scrollBarTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        scrollBarColor = MaterialTheme.colorScheme.secondary,
    )
    return horizontalColumnScrollbar.horizontalScroll(scrollState)
}
