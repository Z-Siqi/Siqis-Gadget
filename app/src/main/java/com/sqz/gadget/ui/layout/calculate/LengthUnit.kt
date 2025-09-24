package com.sqz.gadget.ui.layout.calculate

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.byValue
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.postDelayed
import com.sqz.gadget.KeyboardHeight

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LengthUnitConversion(modifier: Modifier = Modifier) {
    val focus = LocalFocusManager.current
    val isVisible = KeyboardHeight.isVisible

    val unitList = listOf("cm", "m", "inch", "foot")

    val aboveTextState = rememberTextFieldState()
    val belowTextState = rememberTextFieldState()
    var centerValue by rememberSaveable { mutableFloatStateOf(0.0f) }
    var aboveUnitInt by rememberSaveable { mutableIntStateOf(-1) }
    var aboveUnit by rememberSaveable { mutableStateOf(false) }
    var belowUnitInt by rememberSaveable { mutableIntStateOf(-1) }
    var belowUnit by rememberSaveable { mutableStateOf(false) }
    val unitInt = aboveUnitInt != -1 && belowUnitInt != -1
    var aboveCurrent by rememberSaveable { mutableStateOf(false) }
    var belowCurrent by rememberSaveable { mutableStateOf(false) }

    var aboveText by rememberSaveable { mutableStateOf(false) }
    val aboveNoSpace = aboveTextState.text.toString() != ""
    val aboveOneDot = aboveTextState.text.toString().count { it == '.' } <= 1
    if (aboveNoSpace && aboveOneDot && aboveText && unitInt) {
        centerValue = aboveTextState.text.toString().toFloat()
        when (aboveUnitInt) {
            // cm
            0 -> when (belowUnitInt) {
                0 -> editTextState(belowTextState, centerValue)
                1 -> editTextState(belowTextState, (centerValue / 100))
                2 -> editTextState(belowTextState, (centerValue / 2.54f))
                3 -> editTextState(belowTextState, (centerValue / 12 / 2.54f))
            }
            // m
            1 -> when (belowUnitInt) {
                0 -> editTextState(belowTextState, (centerValue * 100))
                1 -> editTextState(belowTextState, centerValue)
                2 -> editTextState(belowTextState, (centerValue * 100 / 2.54f))
                3 -> editTextState(belowTextState, (centerValue * 100 / 12 / 2.54f))
            }
            // inch
            2 -> when (belowUnitInt) {
                0 -> editTextState(belowTextState, (centerValue * 2.54f))
                1 -> editTextState(belowTextState, (centerValue / 39.37f))
                2 -> editTextState(belowTextState, centerValue)
                3 -> editTextState(belowTextState, (centerValue / 12))
            }
            // foot
            3 -> when (belowUnitInt) {
                0 -> editTextState(belowTextState, (centerValue * 12 * 2.54f))
                1 -> editTextState(belowTextState, (centerValue * 0.12f * 2.54f))
                2 -> editTextState(belowTextState, (centerValue * 12))
                3 -> editTextState(belowTextState, centerValue)
            }
        }
        aboveText = false
    } else if (!aboveNoSpace || !aboveOneDot) LaunchedEffect(true) {
        belowTextState.clearText()
        aboveTextState.clearText()
        aboveText = false
    }
    var belowText by rememberSaveable { mutableStateOf(false) }
    val belowNoSpace = belowTextState.text.toString() != ""
    val belowOneDot = belowTextState.text.toString().count { it == '.' } <= 1
    if (belowNoSpace && belowOneDot && belowText && unitInt) {
        centerValue = belowTextState.text.toString().toFloat()
        when (belowUnitInt) {
            // cm
            0 -> when (aboveUnitInt) {
                0 -> editTextState(aboveTextState, centerValue)
                1 -> editTextState(aboveTextState, (centerValue / 100))
                2 -> editTextState(aboveTextState, (centerValue / 2.54f))
                3 -> editTextState(aboveTextState, (centerValue / 12 / 2.54f))
            }
            // m
            1 -> when (aboveUnitInt) {
                0 -> editTextState(aboveTextState, (centerValue * 100))
                1 -> editTextState(aboveTextState, centerValue)
                2 -> editTextState(aboveTextState, (centerValue * 100 / 2.54f))
                3 -> editTextState(aboveTextState, (centerValue * 100 / 12 / 2.54f))
            }
            // inch
            2 -> when (aboveUnitInt) {
                0 -> editTextState(aboveTextState, (centerValue * 2.54f))
                1 -> editTextState(aboveTextState, (centerValue / 39.37f))
                2 -> editTextState(aboveTextState, centerValue)
                3 -> editTextState(aboveTextState, (centerValue / 12))
            }
            // foot
            3 -> when (aboveUnitInt) {
                0 -> editTextState(aboveTextState, (centerValue * 12 * 2.54f))
                1 -> editTextState(aboveTextState, (centerValue * 0.12f * 2.54f))
                2 -> editTextState(aboveTextState, (centerValue * 12))
                3 -> editTextState(aboveTextState, centerValue)
            }
        }
        belowText = false
    } else if (!belowNoSpace || !belowOneDot) LaunchedEffect(true) {
        belowTextState.clearText()
        aboveTextState.clearText()
        belowText = false
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = modifier.padding(top = 24.dp),
                text = "Length Unit Conversion",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
            )
        }
        val padding = modifier.padding(top = 5.dp, bottom = 24.dp)

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 55.dp, bottom = KeyboardHeight.currentDp.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedCard(
                modifier = modifier.padding(16.dp),
                shape = ShapeDefaults.Large,
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
            ) {
                val borderAbove = if (aboveCurrent) {
                    modifier.border(1.dp, MaterialTheme.colorScheme.secondary, ShapeDefaults.Medium)
                } else {
                    modifier
                }
                Column(
                    modifier = modifier.padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 20.dp,
                        bottom = 12.dp
                    ) then borderAbove then padding
                ) {
                    Text(
                        modifier = modifier
                            .align(Alignment.End)
                            .padding(end = 8.dp),
                        text = if (aboveCurrent) "Used unit for conversion" else "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        modifier = modifier
                            .align(Alignment.Start)
                            .padding(start = 35.dp),
                        fontSize = 16.sp,
                        text = "Conversion Unit",
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedCard(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(start = 30.dp, end = 30.dp),
                        shape = CircleShape
                    ) {
                        Box {
                            val isNightModeActive =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) LocalConfiguration.current.isNightModeActive else false
                            val textStyle = if (isNightModeActive) {
                                TextStyle(fontSize = 23.sp).copy(Color.White)
                            } else {
                                TextStyle(fontSize = 23.sp)
                            }
                            BasicTextField(modifier = modifier
                                .fillMaxSize()
                                .padding(start = 16.dp, end = 88.dp, top = 10.dp, bottom = 8.dp)
                                .horizontalScroll(rememberScrollState()),
                                state = aboveTextState,
                                keyboardOptions = KeyboardOptions(
                                    KeyboardCapitalization.None,
                                    autoCorrect = false,
                                    KeyboardType.Number,
                                    ImeAction.Done
                                ),
                                //keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                                textStyle = textStyle,
                                inputTransformation = InputTransformation.byValue { current, new ->
                                    aboveText = true
                                    aboveCurrent = true
                                    belowCurrent = false
                                    if (new.any { it == '.' } && new.contains("""\d""".toRegex())) {
                                        if (new.count { it == '.' } <= 1) {
                                            new.replace("""\D""".toRegex(), ".")
                                        } else {
                                            current.replace("""\D""".toRegex(), "").plus('.')
                                        }
                                    } else {
                                        new.replace("""\D""".toRegex(), "")
                                    }
                                })
                            OutlinedCard(
                                modifier = modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                                    .size(70.dp, 32.dp), shape = ShapeDefaults.Small
                            ) {
                                Box(modifier = modifier
                                    .fillMaxSize()
                                    .clickable {
                                        if (!isVisible) {
                                            aboveUnit = true
                                        } else {
                                            focus.clearFocus()
                                            Handler(Looper.getMainLooper()).postDelayed(300) {
                                                aboveUnit = true
                                            }
                                        }
                                    }) {
                                    Text(
                                        modifier = modifier
                                            .padding(4.dp)
                                            .align(Alignment.Center),
                                        text = if (aboveUnitInt == -1) {
                                            "Unit"
                                        } else {
                                            unitList[aboveUnitInt]
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    DropdownMenu(
                                        expanded = aboveUnit,
                                        onDismissRequest = { aboveUnit = false }
                                    ) {
                                        unitList.forEachIndexed { index, select ->
                                            DropdownMenuItem(onClick = {
                                                if (aboveCurrent) {
                                                    aboveText = true
                                                    belowText = false
                                                } else if (belowCurrent) {
                                                    belowText = true
                                                    aboveText = false
                                                }
                                                aboveUnitInt = index
                                                aboveUnit = false
                                            }, text = {
                                                Text(text = select)
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(modifier.padding(start = 16.dp, end = 16.dp))
                val borderBelow = if (belowCurrent) {
                    modifier.border(1.dp, MaterialTheme.colorScheme.secondary, ShapeDefaults.Medium)
                } else {
                    modifier
                }
                Column(
                    modifier = modifier.padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 12.dp
                    ) then borderBelow then padding
                ) {
                    Text(
                        modifier = modifier
                            .align(Alignment.End)
                            .padding(end = 8.dp),
                        text = if (belowCurrent) "Used unit for conversion" else "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        modifier = modifier
                            .align(Alignment.Start)
                            .padding(start = 35.dp),
                        fontSize = 16.sp,
                        text = "Conversion Unit",
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedCard(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(start = 30.dp, end = 30.dp),
                        shape = CircleShape
                    ) {
                        Box {
                            val isNightModeActive =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) LocalConfiguration.current.isNightModeActive else false
                            val textStyle = if (isNightModeActive) {
                                TextStyle(fontSize = 23.sp).copy(Color.White)
                            } else {
                                TextStyle(fontSize = 23.sp)
                            }
                            BasicTextField(modifier = modifier
                                .fillMaxSize()
                                .padding(start = 16.dp, end = 88.dp, top = 10.dp, bottom = 8.dp)
                                .horizontalScroll(rememberScrollState()),
                                state = belowTextState,
                                keyboardOptions = KeyboardOptions(
                                    KeyboardCapitalization.None,
                                    autoCorrect = false,
                                    KeyboardType.Number,
                                    ImeAction.Done
                                ),
                                //keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                                textStyle = textStyle,
                                inputTransformation = InputTransformation.byValue { current, new ->
                                    belowText = true
                                    belowCurrent = true
                                    aboveCurrent = false
                                    if (new.any { it == '.' } && new.contains("""\d""".toRegex())) {
                                        if (new.count { it == '.' } <= 1) {
                                            new.replace("""\D""".toRegex(), ".")
                                        } else {
                                            current.replace("""\D""".toRegex(), "").plus('.')
                                        }
                                    } else {
                                        new.replace("""\D""".toRegex(), "")
                                    }
                                })
                            OutlinedCard(
                                modifier = modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 16.dp)
                                    .size(70.dp, 32.dp), shape = ShapeDefaults.Small
                            ) {
                                Box(modifier = modifier
                                    .fillMaxSize()
                                    .clickable {
                                        if (!isVisible) {
                                            belowUnit = true
                                        } else {
                                            focus.clearFocus()
                                            Handler(Looper.getMainLooper()).postDelayed(300) {
                                                belowUnit = true
                                            }
                                        }
                                    }) {
                                    Text(
                                        modifier = modifier
                                            .padding(4.dp)
                                            .align(Alignment.Center),
                                        text = if (belowUnitInt == -1) {
                                            "Unit"
                                        } else {
                                            unitList[belowUnitInt]
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    DropdownMenu(
                                        expanded = belowUnit,
                                        onDismissRequest = { belowUnit = false }
                                    ) {
                                        unitList.forEachIndexed { index, select ->
                                            DropdownMenuItem(onClick = {
                                                if (belowCurrent) {
                                                    belowText = true
                                                    aboveText = false
                                                } else if (aboveCurrent) {
                                                    aboveText = true
                                                    belowText = false
                                                }
                                                belowUnitInt = index
                                                belowUnit = false
                                            }, text = {
                                                Text(text = select)
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun editTextState(
    textState: TextFieldState = rememberTextFieldState(),
    formula: Float
): TextFieldState {
    val numberInt = formula.toString().substringAfterLast('.').substringBeforeLast('E').length
    val convert = numberInt.toString().plus('f')
    val convertResult = String.format("%.$convert", formula)
    textState.clearText()
    textState.edit { insert(0, convertResult) }
    return textState
}

@Preview
@Composable
private fun Preview() {
    LengthUnitConversion()
}