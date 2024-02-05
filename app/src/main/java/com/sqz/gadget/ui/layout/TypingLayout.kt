package com.sqz.gadget.ui.layout

import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.placeCursorAtEnd
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.foundation.text2.input.selectAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sqz.gadget.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TypingLayout(navController: NavController, modifier: Modifier = Modifier) {
    val focus = LocalFocusManager.current
    val currentPx = KeyboardHeight.currentPx
    val currentDp = KeyboardHeight.currentDp
    val result = if (KeyboardHeight.currentPx == 0) "Not Calculated" else "$currentPx.px, $currentDp.dp"

    Surface(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { focus.clearFocus() }
            },
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = modifier.padding(10.dp)
            ) {
                Icon(painter = painterResource(R.drawable.back), contentDescription = "back")
            }
        }
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Spacer(modifier = modifier.height(38.dp))
            Text(
                text = "Fix BasicTextField2 Delete Text After Select All Error",
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = modifier.padding(16.dp)
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
            val bottom = currentDp.dp + 50.dp
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

@Suppress("unused")
class KeyboardHeight {
    companion object {
        val currentPx @Composable get() = KeyboardHeight().currentPx()
        val currentDp @Composable get() = KeyboardHeight().toCalculateDp()
        val isVisible @Composable get() = KeyboardHeight().isVisible()
    }

    @Composable
    private fun isVisible(): Boolean {
        return KeyboardHeight().currentPx() != 0
    }

    @Composable
    private fun toCalculateDp(): Int {
        val keyboardHeight = KeyboardHeight().currentPx()
        val density = LocalDensity.current.density
        return (keyboardHeight / density).toInt()
    }

    @Composable
    private fun currentPx(): Int {
        val context = LocalContext.current
        val localScreenHeight =
            (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).toInt()
        val rootView = LocalView.current
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val returnZero = 0
        var height by remember { mutableIntStateOf(0) }
        var notZero by remember { mutableStateOf(false) }
        var calculateNotZeroValue by remember { mutableIntStateOf(0) }
        DisposableEffect(rootView, inputMethodManager) {
            val listener = ViewTreeObserver.OnGlobalLayoutListener {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val keyboardNowHeight = localScreenHeight - rect.bottom
                val keyboardHeight = if (keyboardNowHeight < 0) {
                    notZero = true
                    calculateNotZeroValue = keyboardNowHeight * -1
                    returnZero
                } else {
                    if (notZero) {
                        keyboardNowHeight + calculateNotZeroValue
                    } else {
                        keyboardNowHeight
                    }
                }
                height = keyboardHeight
            }
            rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
            onDispose {
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        }
        return height
    }
}