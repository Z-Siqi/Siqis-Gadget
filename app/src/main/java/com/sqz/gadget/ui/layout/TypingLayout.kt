package com.sqz.gadget.ui.layout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.placeCursorAtEnd
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.foundation.text2.input.selectAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TypingLayout(modifier: Modifier = Modifier) {
    val focus = LocalFocusManager.current

    var defaultSize by remember { mutableIntStateOf(0) }
    var size by remember { mutableIntStateOf(0) }
    val keyboardSize = defaultSize - size
    val toDp = (keyboardSize / LocalDensity.current.density).toInt()
    val result = if (keyboardSize == 0) "Not Calculated" else "$keyboardSize.px, $toDp.dp"

    Spacer(modifier = modifier
        .fillMaxSize()
        .onSizeChanged { defaultSize = it.height })
    Surface(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { focus.clearFocus() }
            },
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Spacer(modifier = modifier
            .fillMaxSize()
            .imePadding()
            .onSizeChanged { size = it.height })
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Fix BasicTextField2 Delete Text After Select All Error",
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = modifier.padding(top = 38.dp, start = 16.dp, end = 16.dp, bottom = 10.dp)
            )
            Text(
                modifier = modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp),
                text = "Keyboard Height: $result"
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val bottom = 100.dp + (toDp - toDp * 0.2).dp
            Card(
                modifier = modifier
                    .fillMaxSize()
                    .padding(50.dp, 150.dp, 50.dp, bottom),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = modifier.padding(16.dp)
                ) {
                    val text2 = rememberTextFieldState()
                    var fixChooseAll by remember { mutableStateOf(false) }
                    if (
                        (!fixChooseAll) &&
                        (text2.text.selectionInChars.length == text2.text.length) &&
                        (text2.text.isNotEmpty())
                    ) {
                        text2.edit { placeCursorAtEnd() }
                        text2.edit { selectAll() }
                        fixChooseAll = true
                    } else if (text2.text.selectionInChars.length < text2.text.length) {
                        fixChooseAll = false
                    }
                    BasicTextField2(
                        state = text2,
                        modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}