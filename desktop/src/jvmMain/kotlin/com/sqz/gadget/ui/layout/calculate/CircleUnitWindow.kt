package com.sqz.gadget.ui.layout.calculate

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.gadget.handler.CircleUnitWindowHandler
import com.sqz.gadget.runtime.InstanceManager
import com.sqz.gadget.ui.common.ContentLayout
import com.sqz.gadget.ui.common.DesktopInputForDouble
import com.sqz.gadget.ui.common.bars.LabelTopAppBar
import com.sqz.gadget.ui.common.getDouble
import com.sqz.gadget.ui.common.setAll
import sqz.gadget.lib.CircleUnit.Companion.Circle

@Composable
fun CircleUnitWindow() {
    val circleUnit: CircleUnitWindowHandler = InstanceManager.getInstance()

    val density = LocalDensity.current
    var parentWidthDp by remember { mutableStateOf(0.dp) }
    val focusManager = LocalFocusManager.current
    val textFieldState = rememberTextFieldState()
    fun clearFocus() {
        focusManager.clearFocus()
        try {
            textFieldState.setAll(textFieldState.getDouble()!!)
        } catch (_: NumberFormatException) {
        } catch (_: NullPointerException) {
        }
    }
    ContentLayout(
        modifier = Modifier.fillMaxWidth().onGloballyPositioned { layoutCoordinates ->
            val widthPx = layoutCoordinates.size.width
            parentWidthDp = with(density) { widthPx.toDp() }
        }.pointerInput(Unit) {
            detectTapGestures { clearFocus() }
        },
        topBar = { LabelTopAppBar("Circle Unit Conversion") }
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.padding(start = 8.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currentUnit = circleUnit.currentUnit.collectAsState().value
            val toUnit = circleUnit.toUnit.collectAsState().value
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TitleText(
                        text = "Current Unit:",
                        modifier = Modifier.padding(top = 10.dp, bottom = 16.dp)
                    )
                    CircleUnitButtons(
                        selected = currentUnit, onClick = {
                            if (currentUnit == it) circleUnit.setCurrent(null) else {
                                circleUnit.setCurrent(it)
                            }
                        }, width = parentWidthDp.value.toInt()
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .width((parentWidthDp / 2.2.dp).dp)
                            .padding(start = 8.dp, end = 8.dp, top = 28.dp),
                        color = DividerDefaults.color
                    )
                    TitleText(
                        text = "Convert To:",
                        modifier = Modifier.padding(top = 15.dp, bottom = 16.dp)
                    )
                    CircleUnitButtons(
                        selected = toUnit, onClick = {
                            if (toUnit == it) circleUnit.setToUnit(null) else {
                                circleUnit.setToUnit(it)
                            }
                        }, drop = currentUnit, enabled = (currentUnit != null).also {
                            if (!it && toUnit != null) circleUnit.setToUnit(null)
                        }, width = parentWidthDp.value.toInt()
                    )
                }
                Column {
                    val enabledInput: Boolean = currentUnit != null && toUnit != null
                    TitleText(
                        text = "Current Number:",
                        modifier = Modifier.padding(top = 8.dp, start = 25.dp)
                    )
                    InputCard(enabled = enabledInput) {
                        DesktopInputForDouble(
                            textFieldState = textFieldState,
                            modifier = Modifier.fillMaxSize(),
                            onKeyboardAction = { clearFocus() },
                            enabled = enabledInput
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp).fillMaxWidth().width(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FilledTonalButton(onClick = {
                            clearFocus()
                            circleUnit.setCalculateValue(textFieldState)
                        }, enabled = enabledInput) {
                            Text(text = "Calculate")
                        }
                        Spacer(modifier = Modifier.width(26.dp))
                        FilledTonalButton(
                            onClick = {
                                textFieldState.clearText()
                                circleUnit.setCalculateValue(null)
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ), enabled = textFieldState.text.isNotEmpty(),
                            content = { Text(text = "Clean") }
                        )
                    }
                    TitleText(
                        text = "Result:",
                        modifier = Modifier.padding(top = 8.dp, start = 25.dp)
                    )
                    ResultCard {
                        val calculateValue = circleUnit.calculateValue.collectAsState().value
                        Text(
                            text = circleUnit.getFormulaString() ?: "Please select the unit",
                            fontSize = 20.sp
                        )
                        if (calculateValue != null && currentUnit != null && toUnit != null) {
                            val calculatedValue = circleUnit.calculate() ?: "ERROR"
                            Text(text = "≈ $calculatedValue", fontSize = 20.sp)
                        }
                        if (circleUnit.getFormulaString() != null) {
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = "A = Area, d = Diameter, C = Circumference, r = Radius",
                                fontSize = 11.sp,
                                modifier = Modifier.align(Alignment.End),
                                lineHeight = 9.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                }
                if ((currentUnit == null || toUnit == null) &&
                    textFieldState.text.isNotEmpty()
                ) {
                    textFieldState.clearText()
                    circleUnit.setCalculateValue(null)
                }
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { InstanceManager.destroy() }
    }
}

@Composable
private fun ResultCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) = Card(
    modifier = modifier
        .requiredWidthIn(min = 100.dp)
        .heightIn(min = 200.dp)
        .fillMaxWidth()
        .padding(start = 26.dp, end = 26.dp, top = 18.dp, bottom = 17.dp)
        .height(IntrinsicSize.Min),
    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        content = content
    )
}

@Composable
private fun InputCard(
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) = OutlinedCard(
    modifier = Modifier
        .requiredWidthIn(min = 100.dp)
        .fillMaxWidth()
        .heightIn(min = 64.dp)
        .padding(start = 28.dp, end = 28.dp, top = 12.dp, bottom = 4.dp)
        .height(IntrinsicSize.Min),
    colors = CardDefaults.outlinedCardColors(
        containerColor = if (enabled) {
            MaterialTheme.colorScheme.surfaceContainerLowest
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        }
    )
) {
    Column(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 8.dp),
        content = content
    )
}

@Composable
private fun CircleUnitButtons(
    selected: Circle?,
    onClick: (Circle) -> Unit,
    width: Int,
    drop: Circle? = null,
    enabled: Boolean = true
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .requiredWidthIn(min = 200.dp)
            .widthIn(max = (width / 2.2).dp)
            .height(IntrinsicSize.Min),
        space = SegmentedButtonDefaults.BorderWidth
    ) {
        val list = Circle.entries.toList().filter { it != drop }
        list.forEachIndexed { index, unit ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index, count = list.size
                ), onClick = { onClick(unit) }, modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                selected = unit == selected, enabled = enabled,
                colors = SegmentedButtonDefaults.colors(
                    disabledInactiveContainerColor = MaterialTheme.colorScheme.surface,
                    disabledInactiveContentColor = MaterialTheme.colorScheme.outlineVariant,
                    disabledInactiveBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Text(
                    text = unit.toStr(),
                    fontSize = 12.sp, lineHeight = 10.sp, textAlign = TextAlign.Justify
                )
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun Circle.toStr(): String {
    return when (this) {
        Circle.Area -> "Area"
        Circle.Diameter -> "Diameter"
        Circle.Circumference -> "Circumference"
        Circle.Radius -> "Radius"
    }
}

@Composable
private fun ColumnScope.TitleText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .align(Alignment.Start)
            .padding(start = 2.dp, end = 16.dp),
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}
