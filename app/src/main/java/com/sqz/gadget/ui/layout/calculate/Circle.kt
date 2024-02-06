package com.sqz.gadget.ui.layout.calculate

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.InputTransformation
import androidx.compose.foundation.text2.input.byValue
import androidx.compose.foundation.text2.input.clearText
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.gadget.ui.layout.KeyboardHeight
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Circle(valueState: ValueState, modifier: Modifier = Modifier) {
    val focus = LocalFocusManager.current
    val textFieldState = rememberTextFieldState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = KeyboardHeight.currentDp.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = modifier.height(50.dp))
        var selectedIndex by remember { mutableIntStateOf(-1) }
        var secondSelectedIndex by remember { mutableIntStateOf(-1) }
        Text(
            modifier = modifier
                .align(Alignment.Start)
                .padding(start = 50.dp),
            text = "Calculate The Circle of"
        )
        Spacer(modifier = modifier.height(8.dp))
        SingleChoiceSegmentedButtonRow(
            modifier = modifier
                .fillMaxWidth()
                .height(38.dp)
                .padding(start = 45.dp, end = 45.dp),
            space = 8.dp
        ) {
            val options = listOf("Area", "Diameter", "Circumference", "Radius")
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = {
                        textFieldState.clearText()
                        valueState.calculateState = false
                        selectedIndex = -1
                        secondSelectedIndex = -1
                        selectedIndex = index
                    },
                    selected = index == selectedIndex
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        lineHeight = 10.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
        if (selectedIndex != -1) {
            Spacer(modifier = modifier.height(8.dp))
            Text(text = "Currently Known Units")
            Spacer(modifier = modifier.height(3.dp))
            SingleChoiceSegmentedButtonRow(
                modifier = modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .padding(start = 50.dp, end = 50.dp),
                space = 8.dp
            ) {
                val options = when (selectedIndex) {
                    0 -> listOf("Diameter", "Circumference", "Radius")
                    1 -> listOf("Area", "Circumference", "Radius")
                    2 -> listOf("Area", "Diameter", "Radius")
                    3 -> listOf("Area", "Diameter", "Circumference")
                    else -> listOf("")
                }
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = {
                            textFieldState.clearText()
                            valueState.calculateState = false
                            secondSelectedIndex = index
                        },
                        selected = index == secondSelectedIndex
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            lineHeight = 10.sp,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        }
        Spacer(modifier = modifier.height(20.dp))
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 50.dp, end = 50.dp)
                .height(60.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                if (valueState.calculateState) {
                    var input by remember { mutableFloatStateOf(0.0F) }
                    if (valueState.onCalculateClick) {
                        input = (textFieldState.text.toString().toFloat())
                        valueState.onCalculateClick = false
                    }
                    SelectionContainer (modifier = modifier.fillMaxSize()) {
                        when (selectedIndex) {
                            // Area
                            0 -> when (secondSelectedIndex) {
                                // A = π(d/2)^2
                                0 -> {
                                    val pi = 3.141592653589793
                                    val calculate = pi * (input / 2).pow(2)
                                    TextOnCard(text = "≈ $calculate")
                                }
                                // A = (C^2)/(4π)
                                1 -> {
                                    val pi = 3.141592653589793
                                    val calculate = (input.pow(2)) / (4 * pi)
                                    TextOnCard(text = "≈ $calculate")
                                }
                                // A = πr^2
                                2 -> {
                                    val pi = 3.141592653589793
                                    val calculate = pi * input.pow(2)
                                    TextOnCard(text = "≈ $calculate")
                                }
                            }
                            // Diameter
                            1 -> when (secondSelectedIndex) {
                                // d = √4A/π
                                0 -> {
                                    val pi = 3.141592653589793
                                    val calculate = sqrt((4 * input) / pi)
                                    TextOnCard(text = "= $calculate")
                                }
                                // d = C/π
                                1 -> {
                                    val pi = 3.141592653589793
                                    val calculate = input / pi
                                    TextOnCard(text = "= $calculate")
                                }
                                // d = 2r
                                2 -> {
                                    val calculate = 2 * input
                                    TextOnCard(text = "= $calculate")
                                }
                            }
                            // Circumference
                            2 -> when (secondSelectedIndex) {
                                // C = 2√πA
                                0 -> {
                                    val pi = 3.141592653589793
                                    val calculate = 2 * sqrt(pi * input)
                                    TextOnCard(text = "≈ $calculate")
                                }
                                // C = πd
                                1 -> {
                                    val pi = 3.141592653589793
                                    val calculate = pi * input
                                    TextOnCard(text = "≈ $calculate")
                                }
                                // C = 2πr
                                2 -> {
                                    val pi = 3.141592653589793
                                    val calculate = 2 * pi * input
                                    TextOnCard(text = "≈ $calculate")
                                }
                            }
                            // Radius
                            3 -> when (secondSelectedIndex) {
                                // r = √A/π
                                0 -> {
                                    val pi = 3.141592653589793
                                    val calculate = sqrt(input / pi)
                                    TextOnCard(text = "≈ $calculate")
                                }
                                // r = d/2
                                1 -> {
                                    val calculate = input / 2
                                    TextOnCard(text = "≈ $calculate")
                                }
                                // r = C/2π
                                2 -> {
                                    val pi = 3.141592653589793
                                    val calculate = input / (2 * pi)
                                    TextOnCard(text = "≈ $calculate")
                                }
                            }
                        }
                    }
                } else {
                    if (selectedIndex == -1) {
                        TextOnCard(text = "Please Select an option")
                    } else {
                        if (secondSelectedIndex == -1) {
                            TextOnCard(text = "Please Select an unit")
                        } else {
                            SelectionContainer {
                                when (selectedIndex) {
                                    0 -> when (secondSelectedIndex) {
                                        0 -> TextOnCard(text = "A = π(d/2)^2")
                                        1 -> TextOnCard(text = "A = C^2/4π")
                                        2 -> TextOnCard(text = "A = πr^2")
                                    }

                                    1 -> when (secondSelectedIndex) {
                                        0 -> TextOnCard(text = "d = √4A/π")
                                        1 -> TextOnCard(text = "d = C/π")
                                        2 -> TextOnCard(text = "d = 2r")
                                    }

                                    2 -> when (secondSelectedIndex) {
                                        0 -> TextOnCard(text = "C = 2√πA")
                                        1 -> TextOnCard(text = "C = πd")
                                        2 -> TextOnCard(text = "C = 2πr")
                                    }

                                    3 -> when (secondSelectedIndex) {
                                        0 -> TextOnCard(text = "r = √A/π")
                                        1 -> TextOnCard(text = "r = d/2")
                                        2 -> TextOnCard(text = "r = C/2π")
                                    }
                                }
                            }
                            Text(
                                text = "A = Area, d = Diameter, C = Circumference, r = Radius",
                                fontSize = 10.sp,
                                modifier = modifier.align(Alignment.BottomEnd),
                                lineHeight = 8.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = modifier.height(35.dp))
        HorizontalDivider(modifier = modifier.padding(start = 16.dp, end = 16.dp))
        Spacer(modifier = modifier.height(30.dp))
        val unit = when (selectedIndex) {
            0 -> when (secondSelectedIndex) {
                0 -> "Diameter"
                1 -> "Circumference"
                2 -> "Radius"
                else -> "Inapplicable"
            }

            1 -> when (secondSelectedIndex) {
                0 -> "Area"
                1 -> "Circumference"
                2 -> "Radius"
                else -> "Inapplicable"
            }

            2 -> when (secondSelectedIndex) {
                0 -> "Area"
                1 -> "Diameter"
                2 -> "Radius"
                else -> "Inapplicable"
            }

            3 -> when (secondSelectedIndex) {
                0 -> "Area"
                1 -> "Diameter"
                2 -> "Circumference"
                else -> "Inapplicable"
            }

            else -> "Inapplicable"
        }
        Text(
            modifier = modifier
                .align(Alignment.Start)
                .padding(start = 55.dp),
            text = "Unit: $unit",
            fontSize = 12.sp
        )
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 50.dp, end = 50.dp),
            shape = CircleShape
        ) {
            BasicTextField2(
                modifier = modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 8.dp),
                state = textFieldState,
                keyboardOptions = KeyboardOptions(
                    KeyboardCapitalization.None,
                    autoCorrect = false,
                    KeyboardType.Number,
                    ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                textStyle = TextStyle(fontSize = 23.sp),
                inputTransformation = InputTransformation.byValue { current, new ->
                    if (new.any { it == '.' } && new.contains("""\d""".toRegex())) {
                        if (new.count { it == '.' } <= 1) {
                            new.replace("""\D""".toRegex(), ".")
                        } else {
                            current.replace("""\D""".toRegex(), "").plus('.')
                        }
                    } else {
                        new.replace("""\D""".toRegex(), "")
                    }
                },
                enabled = selectedIndex != -1 && secondSelectedIndex != -1
            )
        }
        Spacer(modifier = modifier.height(25.dp))
        Row {
            FilledTonalButton(onClick = {
                if (textFieldState.text.isNotEmpty()) {
                    valueState.onCalculateClick = true
                    valueState.calculateState = true
                } else {
                    valueState.calculateState = false
                }
                focus.clearFocus()
            }) {
                Text(text = "Calculate")
            }
            if (textFieldState.text.isNotEmpty()) {
                Spacer(modifier = modifier.width(26.dp))
                FilledTonalButton(
                    onClick = {
                        textFieldState.clearText()
                        valueState.calculateState = false
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(text = "Clean", color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }
        Spacer(modifier = modifier.height(170.dp))
    }
}

@Composable
fun TextOnCard(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Preview
@Composable
private fun Preview() {
    val valueState: ValueState = viewModel()
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Circle(valueState)
    }
}