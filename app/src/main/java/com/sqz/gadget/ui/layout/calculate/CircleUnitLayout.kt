package com.sqz.gadget.ui.layout.calculate

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.gadget.ui.common.BasicTextFieldForDouble
import com.sqz.gadget.ui.common.bars.BackButtonTopAppBar
import com.sqz.gadget.ui.common.bars.verticalScrollWithFixedScrollBar
import com.sqz.gadget.ui.common.getDouble
import com.sqz.gadget.ui.common.setAll
import com.sqz.gadget.viewmodel.CircleUnitViewModel
import com.sqz.gadget.viewmodel.NavViewModel
import sqz.gadget.lib.CircleUnit.Companion.Circle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CircleUnitLayout(
    navViewModel: NavViewModel,
    modifier: Modifier = Modifier,
    viewModel: CircleUnitViewModel = viewModel()
) {
    val state = viewModel.state.collectAsState().value
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
    BackButtonTopAppBar(
        title = "Circle Unit",
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { clearFocus() }
        },
        onBackClick = { navViewModel.requestBack() },
    ) {
        Column(
            modifier = modifier.verticalScrollWithFixedScrollBar(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleText(
                text = "Current Unit:",
                modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
            )
            CircleUnitButtons(
                selected = state.currentUnit, onClick = {
                    if (state.currentUnit == it) viewModel.setCurrentUnit(null) else {
                        viewModel.setCurrentUnit(it)
                    }
                }
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = 38.dp, end = 38.dp, top = 20.dp)
            )
            TitleText(
                text = "Convert To:",
                modifier = Modifier.padding(top = 15.dp, bottom = 16.dp)
            )
            CircleUnitButtons(
                selected = state.toUnit, onClick = {
                    if (state.toUnit == it) viewModel.setToUnit(null) else {
                        viewModel.setToUnit(it)
                    }
                }, drop = state.currentUnit, enabled = (state.currentUnit != null).also {
                    if (!it && state.toUnit != null) viewModel.setToUnit(null)
                }
            )
            Spacer(modifier = Modifier.padding(5.dp))
            HorizontalDivider(modifier = Modifier.padding(20.dp))
            TitleText(
                text = "Current Number:",
                modifier = Modifier
            )
            val enabledInput: Boolean = state.currentUnit != null && state.toUnit != null
            InputCard(enabledInput) {
                BasicTextFieldForDouble(
                    textFieldState = textFieldState,
                    modifier = Modifier.fillMaxSize(),
                    onKeyboardAction = { clearFocus() },
                    enabled = enabledInput
                )
            }
            Row(modifier = Modifier.padding(top = 4.dp)) {
                FilledTonalButton(onClick = {
                    clearFocus()
                    viewModel.setCalculate(textFieldState)
                }, enabled = enabledInput) {
                    Text(text = "Calculate")
                }
                Spacer(modifier = modifier.width(26.dp))
                FilledTonalButton(
                    onClick = {
                        textFieldState.clearText()
                        viewModel.setCalculate(null)
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ), enabled = textFieldState.text.isNotEmpty(),
                    content = { Text(text = "Clean") }
                )
            }
            TitleText(
                text = "Result:",
                modifier = Modifier
            )
            ResultCard {
                Text(
                    text = viewModel.getFormulaString() ?: "Please select the unit",
                    fontSize = 20.sp
                )
                if (state.calculate != null && state.currentUnit != null && state.toUnit != null) {
                    val calculatedValue = viewModel.calculate(state.calculate) ?: "ERROR"
                    Text(text = "≈ $calculatedValue", fontSize = 20.sp)
                }
                if (viewModel.getFormulaString() != null) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "A = Area, d = Diameter, C = Circumference, r = Radius",
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.End),
                        lineHeight = 8.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
        if ((state.currentUnit == null || state.toUnit == null) &&
            textFieldState.text.isNotEmpty()
        ) {
            textFieldState.clearText()
            viewModel.setCalculate(null)
        }
    }
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
private fun ResultCard(
    content: @Composable ColumnScope.() -> Unit
) = Card(
    modifier = Modifier
        .requiredWidthIn(min = 300.dp)
        .heightIn(min = 120.dp)
        .fillMaxWidth()
        .padding(start = 26.dp, end = 26.dp, top = 8.dp, bottom = 17.dp)
        .height(IntrinsicSize.Min),
    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
) {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxSize(),
        content = content
    )
}

@Composable
private fun CircleUnitButtons(
    selected: Circle?,
    onClick: (Circle) -> Unit,
    drop: Circle? = null,
    enabled: Boolean = true
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .requiredWidthIn(min = 300.dp)
            .widthIn(max = 750.dp)
            .fillMaxWidth()
            .padding(start = 35.dp, end = 35.dp)
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
                selected = unit == selected, enabled = enabled
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
private fun ColumnScope.TitleText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .align(Alignment.Start)
            .padding(start = 16.dp, end = 16.dp),
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
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

@Preview
@Composable
private fun Preview() {
    CircleUnitLayout(
        navViewModel = viewModel()
    )
}
