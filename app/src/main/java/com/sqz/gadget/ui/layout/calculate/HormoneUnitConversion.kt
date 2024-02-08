package com.sqz.gadget.ui.layout.calculate

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.InputTransformation
import androidx.compose.foundation.text2.input.byValue
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.postDelayed
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.gadget.KeyboardHeight
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HormoneUnitConversion(valueState: ValueState, modifier: Modifier = Modifier) {

    val focus = LocalFocusManager.current
    val textFieldState = rememberTextFieldState()
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var input by remember { mutableFloatStateOf(0.0F) }
    val unitList = listOf(
        /*Line 1*/"pmol/L", "pg/mL", "pg/dL", "pg/L",
        /*Line 2*/"ng/L", "nmol/L", "ng/mL", "ng/dL",
        /*Line 3*/"µg/L", "μg/dL", "mIU/L", "μIU/mL",
        /*Line 4*/"pg/100mL", "ng%", "ng/100mL", "pg%",
        /*Line 5*/"IU/L", "IU/dL", "IU/mL", "mIU/mL",
    )
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var unit by remember { mutableIntStateOf(-1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = KeyboardHeight.currentDp.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(70.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = modifier.padding(top = 24.dp),
                text = "Hormone Units Conversion",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
            )
        }
        Text(
            modifier = modifier
                .align(Alignment.Start)
                .padding(start = 40.dp),
            fontSize = 16.sp,
            text = "Units used for conversion (Hormone)",
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = modifier.height(8.dp))
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 50.dp, end = 50.dp),
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
                BasicTextField2(modifier = modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 88.dp, top = 10.dp, bottom = 8.dp),
                    state = textFieldState,
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.None,
                        autoCorrect = false,
                        KeyboardType.Number,
                        ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focus.clearFocus() }),
                    textStyle = textStyle,
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
                    })
                OutlinedCard(
                    modifier = modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(70.dp, 32.dp), shape = ShapeDefaults.Small
                ) {
                    val isVisible = KeyboardHeight.isVisible
                    Box(modifier = modifier
                        .fillMaxSize()
                        .clickable {
                            if (!isVisible) {
                                showBottomSheet = true
                            } else {
                                focus.clearFocus()
                                Handler(Looper.getMainLooper()).postDelayed(300) {
                                    showBottomSheet = true
                                }
                            }
                        }) {
                        Text(
                            modifier = modifier
                                .padding(4.dp)
                                .align(Alignment.Center),
                            text = if (unit == -1) {
                                "Unit"
                            } else {
                                unitList[unit]
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
        Spacer(modifier = modifier.height(18.dp))
        FilledTonalButton(
            onClick = {
                if (textFieldState.text.isNotEmpty() && unit != -1) {
                    valueState.onCalculateClick = true
                    valueState.calculateState = true
                }
                focus.clearFocus()
            }, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(text = "Calculate", color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
        val height = if (LocalConfiguration.current.screenHeightDp <= 600) 12.dp else 30.dp
        Spacer(modifier = modifier.height(height))
        HorizontalDivider(modifier = modifier.padding(start = 16.dp, end = 16.dp))
        Spacer(modifier = modifier.height(20.dp))
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(32.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                modifier = modifier
                    .padding(start = 40.dp),
                fontSize = 16.sp,
                text = "Conversion Unit to (Hormone)",
                fontWeight = FontWeight.Medium
            )
            if (valueState.calculateState) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    val end = if (screenWidth > 530) 80.dp else 40.dp
                    val width = if (screenWidth > 530) 160.dp else 120.dp
                    FilledTonalButton(
                        modifier = modifier
                            .size(width, 32.dp)
                            .padding(end = end),
                        onClick = { valueState.calculateState = false },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            text = "Reset",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        Spacer(modifier = modifier.height(10.dp))
        val cardHeight = if (LocalConfiguration.current.screenHeightDp <= 610) {
            238.dp
        } else {
            258.dp
        }
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 35.dp, end = 35.dp)
                .height(cardHeight),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            var originalUnit by remember { mutableStateOf("N/A") }
            var unit0 by remember { mutableFloatStateOf(0F) }
            var unit1 by remember { mutableFloatStateOf(0F) }
            var unit2 by remember { mutableFloatStateOf(0F) }
            var unit3 by remember { mutableFloatStateOf(0F) }
            var unit4 by remember { mutableFloatStateOf(0F) }
            var unit5 by remember { mutableFloatStateOf(0F) }
            var unit6 by remember { mutableFloatStateOf(0F) }
            var unit7 by remember { mutableFloatStateOf(0F) }
            var unit8 by remember { mutableFloatStateOf(0F) }
            var unit9 by remember { mutableFloatStateOf(0F) }
            var unit10 by remember { mutableFloatStateOf(0F) }
            var unit11 by remember { mutableFloatStateOf(0F) }
            var unit12 by remember { mutableFloatStateOf(0F) }
            var unit130 by remember { mutableFloatStateOf(0F) }
            var unit14 by remember { mutableFloatStateOf(0F) }
            var unit15 by remember { mutableFloatStateOf(0F) }
            var unit16 by remember { mutableFloatStateOf(0F) }
            var unit17 by remember { mutableFloatStateOf(0F) }
            var unit18 by remember { mutableFloatStateOf(0F) }
            var unit19 by remember { mutableFloatStateOf(0F) }

            if (valueState.onCalculateClick) {
                if (unit != -1) {
                    originalUnit = unitList[unit]
                    input = (textFieldState.text.toString().toFloat())
                    when (unit) {
                        //pmol/L
                        0 -> {
                            unit0 = input
                            unit1 = input * 0.27240533914464726.toFloat()
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit4 = unit1
                            unit5 = unit2 / 1000 / 28.81844380403458.toFloat()
                            unit6 = unit4 / 1000
                            unit7 = unit4 / 10
                            unit8 = unit4 / 1000
                            unit9 = unit8 / 10
                            unit10 = unit7 * 10 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // pg/mL
                        1 -> {
                            unit1 = input
                            unit0 = input / 0.27240533914464726.toFloat()
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit4 = unit1
                            unit5 = unit2 / 1000 / 28.81844380403458.toFloat()
                            unit6 = unit4 / 1000
                            unit7 = unit4 / 10
                            unit8 = unit4 / 1000
                            unit9 = unit8 / 10
                            unit10 = unit7 * 10 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // pg/dL
                        2 -> {
                            unit2 = input
                            unit0 = input / 100 / 0.27240533914464726.toFloat()
                            unit1 = unit2 / 100
                            unit3 = unit1 * 1000
                            unit4 = unit1
                            unit5 = unit2 / 1000 / 28.81844380403458.toFloat()
                            unit6 = unit4 / 1000
                            unit7 = unit4 / 10
                            unit8 = unit4 / 1000
                            unit9 = unit8 / 10
                            unit10 = unit7 * 10 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // pg/L
                        3 -> {
                            unit3 = input
                            unit0 = input / 1000 / 0.27240533914464726.toFloat()
                            unit1 = unit3 / 1000
                            unit2 = unit3 / 10
                            unit4 = unit1
                            unit5 = unit2 / 1000 / 28.81844380403458.toFloat()
                            unit6 = unit4 / 1000
                            unit7 = unit4 / 10
                            unit8 = unit4 / 1000
                            unit9 = unit8 / 10
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // ng/L
                        4 -> {
                            unit4 = input
                            unit0 = input / 0.27240533914464726.toFloat()
                            unit1 = unit4
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit5 = unit2 / 1000 / 28.81844380403458.toFloat()
                            unit6 = unit4 / 1000
                            unit7 = unit4 / 10
                            unit8 = unit4 / 1000
                            unit9 = unit8 / 10
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // nmol/L
                        5 -> {
                            unit5 = input
                            unit4 = input * 28.81844380403458.toFloat() * 10
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit1 = unit4
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit6 = unit4 / 1000
                            unit7 = input * 28.81844380403458.toFloat()
                            unit8 = unit4 / 1000
                            unit9 = unit8 / 10
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // ng/mL
                        6 -> {
                            unit6 = input
                            unit7 = unit6 * 100
                            unit4 = unit7 * 10
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit1 = unit4
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit5 = unit7 / 28.81844380403458.toFloat()
                            unit8 = unit6
                            unit9 = unit8 / 10
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // ng/dL
                        7 -> {
                            unit7 = input
                            unit4 = unit7 * 10
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit1 = unit4
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit5 = unit7 / 28.81844380403458.toFloat()
                            unit6 = unit4 / 1000
                            unit8 = unit6
                            unit9 = unit8 / 10
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // μg/L
                        8 -> {
                            unit8 = input
                            unit4 = unit8 * 1000
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit1 = unit4
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit6 = unit8
                            unit7 = unit6 * 100
                            unit9 = unit8 / 10
                            unit5 = unit7 / 28.81844380403458.toFloat()
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // μg/dL
                        9 -> {
                            unit9 = input
                            unit8 = unit9 * 10
                            unit4 = unit8 * 1000
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit1 = unit4
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit6 = unit8
                            unit7 = unit6 * 100
                            unit5 = unit7 / 28.81844380403458.toFloat()
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // mIU/L
                        10 -> {
                            unit10 = input
                            unit8 = unit10 / 1000 * 47
                            unit9 = unit8 / 10
                            unit7 = input / 10 * 47
                            unit6 = unit8
                            unit5 = unit7 / 28.81844380403458.toFloat()
                            unit4 = unit8 * 1000
                            unit3 = unit4 * 1000
                            unit2 = unit4 * 100
                            unit1 = unit4
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // μIU/mL
                        11 -> {
                            unit11 = input
                            unit10 = unit11
                            unit8 = unit10 / 1000 * 47
                            unit9 = unit8 / 10
                            unit7 = input / 10 * 47
                            unit6 = unit8
                            unit5 = unit7 / 28.81844380403458.toFloat()
                            unit4 = unit8 * 1000
                            unit3 = unit4 * 1000
                            unit2 = unit4 * 100
                            unit1 = unit4
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // pg/100mL
                        12 -> {
                            unit12 = input
                            unit1 = input / 100
                            unit0 = unit1 / 0.27240533914464726.toFloat()
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit4 = unit1
                            unit5 = unit2 / 1000 / 28.81844380403458.toFloat()
                            unit6 = unit4 / 1000
                            unit7 = unit4 / 10
                            unit8 = unit4 / 1000
                            unit9 = unit8 / 10
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // ng%
                        13 -> {
                            unit130 = input
                            unit14 = unit130
                            unit7 = input
                            unit4 = input * 10
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit1 = unit4
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit5 = unit2 / 1000 / 28.81844380403458.toFloat()
                            unit6 = unit4 / 1000
                            unit7 = unit6 * 100
                            unit8 = unit4 / 1000
                            unit9 = unit8 / 10
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // ng/100mL
                        14 -> {
                            unit14 = input
                            unit7 = input
                            unit4 = input * 10
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit1 = unit4
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit5 = unit2 / 1000 / 28.81844380403458.toFloat()
                            unit6 = unit4 / 1000
                            unit7 = unit6 * 100
                            unit8 = unit4 / 1000
                            unit9 = unit8 / 10
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit12 = unit2
                            unit15 = unit2
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // pg%
                        15 -> {
                            unit15 = input
                            unit12 = unit15
                            unit1 = unit12 / 100
                            unit0 = unit1 / 0.27240533914464726.toFloat()
                            unit2 = unit1 * 100
                            unit3 = unit1 * 1000
                            unit4 = unit1
                            unit5 = unit2 / 1000 / 28.81844380403458.toFloat()
                            unit6 = unit4 / 1000
                            unit7 = unit4 / 10
                            unit8 = unit4 / 1000
                            unit9 = unit8 / 10
                            unit10 = unit8 * 1000 / 47
                            unit11 = unit10
                            unit14 = unit7
                            unit130 = unit7
                            unit16 = unit11 / 1000
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // IU/L
                        16 -> {
                            unit16 = input
                            unit11 = unit16 * 1000
                            unit10 = unit11
                            unit8 = unit10 / 1000 * 47
                            unit9 = unit8 / 10
                            unit7 = input / 10 * 47
                            unit6 = unit8
                            unit5 = unit7 / 28.81844380403458.toFloat()
                            unit4 = unit8 * 1000
                            unit3 = unit4 * 1000
                            unit2 = unit4 * 100
                            unit1 = unit4
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // IU/dL
                        17 -> {
                            unit17 = input
                            unit16 = unit17 * 10
                            unit11 = unit16 * 1000
                            unit10 = unit11
                            unit8 = unit10 / 1000 * 47
                            unit9 = unit8 / 10
                            unit7 = input / 10 * 47
                            unit6 = unit8
                            unit5 = unit7 / 28.81844380403458.toFloat()
                            unit4 = unit8 * 1000
                            unit3 = unit4 * 1000
                            unit2 = unit4 * 100
                            unit1 = unit4
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit18 = unit16 / 1000
                            unit19 = unit16
                        }
                        // IU/mL
                        18 -> {
                            unit18 = input
                            unit16 = unit18 * 1000
                            unit11 = unit16 * 1000
                            unit10 = unit11
                            unit8 = unit10 / 1000 * 47
                            unit9 = unit8 / 10
                            unit7 = input / 10 * 47
                            unit6 = unit8
                            unit5 = unit7 / 28.81844380403458.toFloat()
                            unit4 = unit8 * 1000
                            unit3 = unit4 * 1000
                            unit2 = unit4 * 100
                            unit1 = unit4
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit17 = unit16 / 10
                            unit19 = unit16
                        }
                        // mIU/mL
                        19 -> {
                            unit19 = input
                            unit16 = unit19
                            unit11 = unit16 * 1000
                            unit10 = unit11
                            unit8 = unit10 / 1000 * 47
                            unit9 = unit8 / 10
                            unit7 = input / 10 * 47
                            unit6 = unit8
                            unit5 = unit7 / 28.81844380403458.toFloat()
                            unit4 = unit8 * 1000
                            unit3 = unit4 * 1000
                            unit2 = unit4 * 100
                            unit1 = unit4
                            unit0 = unit4 / 0.27240533914464726.toFloat()
                            unit12 = unit2
                            unit15 = unit2
                            unit14 = unit7
                            unit130 = unit7
                            unit17 = unit16 / 10
                            unit18 = unit16 / 1000
                        }
                        else -> {
                            Log.e("SqGadgetTag", "ERROR TO CONVERT")
                        }
                    }
                }
                valueState.onCalculateClick = false
            }
            Text(
                text = "Original Unit: $originalUnit",
                fontSize = 12.sp,
                lineHeight = 12.sp,
                textAlign = TextAlign.Justify,
                maxLines = 1,
                modifier = modifier.padding(start = 5.dp, top = 3.dp)
            )
            SelectionContainer {
                Row(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .weight(1f)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        TextResult(text = "pmol/L: ${unit0.toStringFix(valueState)}")
                        TextResult(text = "pg/mL: ${unit1.toStringFix(valueState)}")
                        TextResult(text = "pg/dL: ${unit2.toStringFix(valueState)}")
                        TextResult(text = "pg/L: ${unit3.toStringFix(valueState)}")
                        TextResult(text = "ng/L: ${unit4.toStringFix(valueState)}")
                        TextResult(text = "nmol/L: ${unit5.toStringFix(valueState)}")
                        TextResult(text = "ng/mL: ${unit6.toStringFix(valueState)}")
                        TextResult(text = "ng/dL: ${unit7.toStringFix(valueState)}")
                        TextResult(text = "μg/L: ${unit8.toStringFix(valueState)}")
                        TextResult(text = "μg/dL: ${unit9.toStringFix(valueState)}")
                    }
                    VerticalDivider(modifier.padding(3.dp))
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .weight(1f)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        TextResult(text = "mIU/L: ${unit10.toStringFix(valueState)}")
                        TextResult(text = "μIU/mL: ${unit11.toStringFix(valueState)}")
                        TextResult(text = "pg/100mL: ${unit12.toStringFix(valueState)}")
                        TextResult(text = "ng/%: ${unit130.toStringFix(valueState)}")
                        TextResult(text = "ng/100mL: ${unit14.toStringFix(valueState)}")
                        TextResult(text = "pg%: ${unit15.toStringFix(valueState)}")
                        TextResult(text = "IU/L: ${unit16.toStringFix(valueState)}")
                        TextResult(text = "IU/dL: ${unit17.toStringFix(valueState)}")
                        TextResult(text = "IU/mL: ${unit18.toStringFix(valueState)}")
                        TextResult(text = "mIU/mL: ${unit19.toStringFix(valueState)}")
                    }
                }
            }
        }
        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 1.dp, end = 50.dp),
            text = "tolerance scope: < 0.1",
            textAlign = TextAlign.End,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (LocalConfiguration.current.screenHeightDp >= 740) {
            Spacer(modifier = modifier.height(20.dp))
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 35.dp, end = 35.dp)
                    .height(125.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { showAlertDialog = true }
                    },
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .horizontalScroll(rememberScrollState())
                        .padding(7.dp)
                ) {
                    TextOfNote(
                        text = "Normal Range of Hormones (Approximate range)",
                        fontWeight = FontWeight.Medium
                    )
                    TextOfNote(text = "Estradiol (E2): Female: 30 ~ 400 pg/mL. Male <50 pg/mL")
                    TextOfNote(text = "Testosterone (T): Female: <70, Male 300~1000 ng/dL")
                    TextOfNote(text = "Progesterone (P): Male <0.149, Female 0.57 ~ 23.9 ng/mL")
                    TextOfNote(text = "Luteinizing (LH): Male 1.24~7.8, Female 1.68~56.6 IU/L")
                    TextOfNote(text = "Follicle-Stimulating (FSH): 1.4 ~ 17.2 mIU/L")
                    TextOfNote(text = "Prolactin (PRL): Male <20 ng/mL. Female <25 ng/mL")
                    TextOfNote(
                        text = "Click to view details",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Light,
                        modifier = modifier.align(Alignment.End)
                    )
                }
            }
        } else {
            val start = if (screenWidth >= 375) 58.dp else 45.dp
            AssistChip(
                modifier = modifier
                    .align(Alignment.Start)
                    .padding(start = start)
                    .size(180.dp, 30.dp),
                onClick = { showAlertDialog = true },
                label = {
                    Text(
                        text = "Click to view the normal range of hormones",
                        fontSize = 12.sp,
                        letterSpacing = 0.sp,
                        lineHeight = 11.sp
                    )
                }
            )
        }
    }
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAlertDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            title = {
                Text("Normal Range of Hormones")
            },
            text = {
                Column(
                    modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Estradiol (E2): 30 to 400 pg/mL for premenopausal female; 0 to 30 pg/mL for postmenopausal female. 10 to 50 pg/mL for male.")
                    Text("Testosterone (T): 300 to 1,000 ng/dL for male. 15 to 70 ng/dL for female.")
                    Text("Progesterone (P): 0.1 to 0.7 ng/mL in the follicular stage of the menstrual cycle; 2 to 25 ng/mL in the luteal stage of the menstrual cycle; 10 to 290 ng/mL in pregnancy. 0.13–0.97 ng/mL for male.")
                    Text("Luteinizing Hormone (LH): Female, follicular phase of menstrual cycle: 1.68 to 15 IU/L; Female, midcycle peak: 21.9 to 56.6 IU/L. 1.24 to 7.8 IU/L for male.")
                    Text("Follicle-Stimulating Hormone (FSH): 4.7 to 21.5 mIU/mL for female; 25.8 to 134.8 mIU/mL for postmenopausal female. 1.5 to 12.4 mIU/mL for male.")
                    Text("Prolactin (PRL): less than 25 ng/mL for female; 80 to 400 ng/mL for pregnant female. less than 20 ng/mL for male.")
                }
            }
        )
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = modifier
                .height((LocalConfiguration.current.screenHeightDp - 40).dp)
                .fillMaxWidth(), onDismissRequest = {
                showBottomSheet = false
            }, sheetState = sheetState
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(7.dp)
            ) {
                var buttonOfUnit by remember { mutableIntStateOf(0) }
                val rowUnits = listOf(4, 4, 4, 4, 4)
                rowUnits.forEach { itemCount ->
                    Row(
                        modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(itemCount) {
                            if (buttonOfUnit < unitList.size) {
                                val text = unitList[buttonOfUnit]
                                val intText = unitList.lastIndexOf(unitList[buttonOfUnit])
                                Chip(onClick = {
                                    unit = intText
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                        }
                                    }
                                }, label = { TextOfUnit(text = text) })
                                buttonOfUnit++
                            }
                        }
                    }
                    Spacer(modifier = modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun TextOfNote(
    modifier: Modifier = Modifier,
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Justify,
) {
    Text(
        text = text,
        fontSize = 12.sp,
        letterSpacing = 0.sp,
        lineHeight = 11.sp,
        maxLines = 1,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = fontWeight,
        textAlign = textAlign,
        modifier = modifier
    )
}

@Composable
private fun Float.toStringFix(valueState: ValueState): String {
    val numberInt = this.toString().substringAfterLast('.').substringBeforeLast('-').length
    val convert = numberInt.toString().plus('f')
    val output = if (!String.format("%.$convert", this).endsWith("00")) {
        String.format("%.$convert", this)
    } else if (String.format("%.$convert", this).endsWith("0.0")) {
        String.format("%.$convert", this)
    } else {
        String.format("%.$convert", this).plus('1')
    }
    return if (valueState.calculateState) {
        output
    } else {
        "- - -"
    }
}

@Composable
private fun TextResult(text: String, modifier: Modifier = Modifier) {
    val padding = if (LocalConfiguration.current.screenHeightDp <= 610) {
        2.dp
    } else {
        3.dp
    }
    Text(
        text = text,
        fontSize = (15 - 1).sp,
        lineHeight = 12.sp,
        maxLines = 1,
        textAlign = TextAlign.Justify,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(padding)
    )
}

@Composable
private fun Chip(
    onClick: () -> Unit, label: @Composable () -> Unit, modifier: Modifier = Modifier
) {
    val size = when (LocalConfiguration.current.screenWidthDp) {
        in 1..375 -> modifier.size(70.dp, 45.dp)
        in 376..409 -> modifier.size(90.dp, 35.dp)
        in 410..550 -> modifier.size(100.dp, 38.dp)
        else -> modifier.size(120.dp, 40.dp)
    }
    val padding = when (LocalConfiguration.current.screenWidthDp) {
        in 1..375 -> modifier.padding(start = 5.dp, end = 5.dp)
        in 376..409 -> modifier.padding(start = 7.dp, end = 7.dp)
        in 410..550 -> modifier.padding(start = 8.dp, end = 8.dp)
        else -> modifier.padding(start = 12.dp, end = 12.dp)
    }
    AssistChip(
        modifier = size then padding,
        onClick = onClick,
        label = label
    )
}

@Composable
private fun TextOfUnit(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Justify,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Preview
@Composable
private fun Preview() {
    val valueState: ValueState = viewModel()
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        HormoneUnitConversion(valueState)
    }
}