package com.sqz.gadget.ui.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.byValue
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BasicTextFieldForDouble(
    textFieldState: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textSize: Int = 23,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    cursorBrushColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onKeyboardAction: KeyboardActionHandler? = null,
    scrollState: ScrollState = rememberScrollState()
) {
    BasicTextField(
        modifier = modifier,
        state = textFieldState,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
        textStyle = TextStyle(
            color = textColor,
            fontSize = textSize.sp
        ),
        onKeyboardAction = onKeyboardAction,
        cursorBrush = SolidColor(cursorBrushColor),
        inputTransformation = InputTransformation.byValue { current, new ->
            if (new.count { it == '.' } > 1) { // if found more than 1 dot
                if (current.length == 1) { // if try to put a dot when already got only 1 dot character here
                    // replace to 0
                    current.replace("""\.""".toRegex(), "0")
                } else { // if try to put a dot but the number already got a dot
                    // replace . to end
                    current.replace("""\.""".toRegex(), "").plus(".")
                }
            } else { // if not found more than 1 dot
                // remove all non-digit and more than 1 dot
                new.replace("""[^\d.]|\.{2,}""".toRegex(), "")
            }
        },
        enabled = enabled,
        scrollState = scrollState,
        lineLimits = TextFieldLineLimits.SingleLine
    )
}

fun TextFieldState.setAll(double: Double) {
    this.clearText()
    this.edit {
        insert(0, double.toString())
    }
}

/**
 * Get [TextFieldState] as [Double] number and returns the result.
 * @throws NumberFormatException if the string is not a valid representation of a number.
 */
fun TextFieldState.getDouble(): Double? {
    if (this.text.isEmpty() || this.text.isBlank()) {
        return null
    }
    var text = this.text.toString()
    if (text.startsWith('.')) {
        text = "0$text"
    }
    if (text.endsWith('.')) {
        text = text + "0"
    }
    return text.toDouble()
}
