package com.sqz.gadget.ui.layout.calculate

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.text.BasicTextField2
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.byValue
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.round

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HormoneUnitConversion(valueState: ValueState, modifier: Modifier = Modifier) {

    val focus = LocalFocusManager.current
    val textFieldState = rememberTextFieldState()
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    var input by remember { mutableFloatStateOf(0.0F) }
    val unitList = listOf(
        /*Line 1*/"pmol/L", "pmol/mL", "pmol/dL",
        /*Line 2*/"pg/L", "pg/mL", "pg/dL", "pg%",
        /*Line 3*/"nmol/L", "nmol/mL", "nmol/dL",
        /*Line 4*/"ng/L", "ng/mL", "ng/dL", "ng%",
        /*Line 5*/"µg/L", "µg/mL", "µg/dL",
        /*Line 6*/"pg/100mL", "ng/100mL",
        /*Line 7*/"mIU/L", "mIU/mL", "mIU/dL",
        /*Line 8*/"IU/L", "IU/mL", "IU/dL",
        /*Line 9*/"μIU/L", "μIU/mL", "μIU/dL",
    )
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var unit by remember { mutableIntStateOf(-1) }
    var resultColor by remember { mutableIntStateOf(-1) }
    var preResultColor by remember { mutableIntStateOf(-1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = KeyboardHeight.currentDp.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val titleHeight = when (screenHeight) {
            in 0 .. 670 -> 50.dp
            in 671..810 -> 55.dp
            else -> 70.dp
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(titleHeight),
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
        val heightOnButton = when (screenHeight) {
            in 0 .. 670 -> 5.dp
            in 671..810 -> 8.dp
            else -> 18.dp
        }
        Spacer(modifier = modifier.height(heightOnButton))
        var showToast by remember { mutableStateOf(false) }
        FilledTonalButton(
            onClick = {
                if (textFieldState.text.isNotEmpty() && unit != -1) {
                    valueState.onCalculateClick = true
                    valueState.calculateState = true
                } else if (unit == -1) showToast = true
                focus.clearFocus()
            }, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(text = "Calculate", color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
        if (showToast) {
            Toast.makeText(
                context,
                "Please select an unit!",
                Toast.LENGTH_SHORT
            ).show()
            LaunchedEffect(true) {
                delay(3000)
                showToast = false
            }
        }
        val heightOnLine = when (screenHeight) {
            in 0..599 -> 10.dp
            in 600..810 -> 16.dp
            else -> 25.dp
        }
        Spacer(modifier = modifier.height(heightOnLine))
        HorizontalDivider(modifier = modifier.padding(start = 16.dp, end = 16.dp))
        val heightUnderLine = when (screenHeight) {
            in 0 .. 670 -> 5.dp
            in 671..810 -> 12.dp
            else -> 20.dp
        }
        Spacer(modifier = modifier.height(heightUnderLine))
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(30.dp),
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
        val cardHeight = when (LocalConfiguration.current.screenHeightDp) {
            in 0..570 -> 238.dp
            in 571..610 -> 268.dp
            in 611..850 -> 330.dp
            else -> 350.dp
        }
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 35.dp, end = 35.dp)
                .height(cardHeight),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            var originalUnit by remember { mutableStateOf("N/A") }
            var unit001 by remember { mutableFloatStateOf(0F) } //pmol/L
            var unit002 by remember { mutableFloatStateOf(0F) } //pmol/mL
            var unit003 by remember { mutableFloatStateOf(0F) } //pmol/dL
            var unit101 by remember { mutableFloatStateOf(0F) } //pg/L
            var unit102 by remember { mutableFloatStateOf(0F) } //pg/mL
            var unit103 by remember { mutableFloatStateOf(0F) } //pg/dL
            var unit104 by remember { mutableFloatStateOf(0F) } //pg%
            var unit201 by remember { mutableFloatStateOf(0F) } //nmol/L
            var unit202 by remember { mutableFloatStateOf(0F) } //nmol/mL
            var unit203 by remember { mutableFloatStateOf(0F) } //nmol/dL
            var unit301 by remember { mutableFloatStateOf(0F) } //ng/L
            var unit302 by remember { mutableFloatStateOf(0F) } //ng/mL
            var unit303 by remember { mutableFloatStateOf(0F) } //ng/dL
            var unit304 by remember { mutableFloatStateOf(0F) } //ng%
            var unit401 by remember { mutableFloatStateOf(0F) } //µg/L
            var unit402 by remember { mutableFloatStateOf(0F) } //µg/mL
            var unit403 by remember { mutableFloatStateOf(0F) } //µg/dL
            var unit501 by remember { mutableFloatStateOf(0F) } //mIU/L
            var unit502 by remember { mutableFloatStateOf(0F) } //mIU/mL
            var unit503 by remember { mutableFloatStateOf(0F) } //mIU/dL
            var unit601 by remember { mutableFloatStateOf(0F) } //IU/L
            var unit602 by remember { mutableFloatStateOf(0F) } //IU/mL
            var unit603 by remember { mutableFloatStateOf(0F) } //IU/dL
            var unit701 by remember { mutableFloatStateOf(0F) } //μIU/L
            var unit702 by remember { mutableFloatStateOf(0F) } //μIU/mL
            var unit703 by remember { mutableFloatStateOf(0F) } //μIU/dL


            if (valueState.onCalculateClick) {
                var unitNumber by remember { mutableIntStateOf(-1) }
                var unitCalculate by remember { mutableFloatStateOf(0F) }
                if (unit != -1) {
                    originalUnit = unitList[unit]
                    input = (textFieldState.text.toString().toFloat())
                    when (unit) {
                        /** pmol/L **/
                        0 -> {
                            unitCalculate = input
                            unitNumber = 0
                        }
                        // pmol/mL
                        1 -> {
                            unitCalculate = input * 1000
                            unitNumber = 0
                        }
                        // pmol/dL
                        2 -> {
                            unitCalculate = input * 10
                            unitNumber = 0
                        }
                        /* pg/L */
                        3 -> {
                            unitCalculate = input / 1000 / 0.27240533914464726.toFloat()
                            unitNumber = 0
                        }
                        // pg/mL
                        4 -> {
                            unitCalculate = input / 0.27240533914464726.toFloat()
                            unitNumber = 0
                        }
                        // pg/dL
                        5 -> {
                            unitCalculate = input / 100 / 0.27240533914464726.toFloat()
                            unitNumber = 0
                        }
                        /** nmol/L **/
                        7 -> {
                            unitCalculate = input
                            unitNumber = 1
                        }
                        // nmol/mL
                        8 -> {
                            unitCalculate = input * 1000
                            unitNumber = 1
                        }
                        // nmol/dL
                        9 -> {
                            unitCalculate = input * 10
                            unitNumber = 1
                        }
                        /* ng/L */
                        10 -> {
                            unitCalculate = input / 10 / 28.81844380403458.toFloat()
                            unitNumber = 1
                        }
                        // ng/mL
                        11 -> {
                            unitCalculate = input * 100 / 28.81844380403458.toFloat()
                            unitNumber = 1
                        }
                        // ng/dL
                        12 -> {
                            unitCalculate = input / 28.81844380403458.toFloat()
                            unitNumber = 1
                        }
                        // ng%
                        13 -> {
                            unitCalculate = input / 28.81844380403458.toFloat()
                            unitNumber = 1
                        }
                        /** µg/L **/
                        14 -> {
                            unitCalculate = input
                            unitNumber = 2
                        }
                        // µg/mL
                        15 -> {
                            unitCalculate = input * 1000
                            unitNumber = 2
                        }
                        // µg/dL
                        16 -> {
                            unitCalculate = input * 10
                            unitNumber = 2
                        }
                        // pg/100mL
                        17 -> {
                            unitCalculate = input / 100 / 0.27240533914464726.toFloat()
                            unitNumber = 0
                        }
                        // ng/100mL
                        18 -> {
                            unitCalculate = input / 28.81844380403458.toFloat()
                            unitNumber = 1
                        }
                        /** mIU/L **/
                        19 -> {
                            unitCalculate = input * 0.047.toFloat()
                            unitNumber = 2
                        }
                        // mIU/mL
                        20 -> {
                            unitCalculate = input * 1000 * 0.047.toFloat()
                            unitNumber = 2
                        }
                        // mIU/dL
                        21 -> {
                            unitCalculate = input * 10 * 0.047.toFloat()
                            unitNumber = 2
                        }
                        /* IU/L */
                        22 -> {
                            unitCalculate = input * 1000 * 0.047.toFloat()
                            unitNumber = 2
                        }
                        // IU/mL
                        23 -> {
                            unitCalculate = input * 1000 * 1000 * 0.047.toFloat()
                            unitNumber = 2
                        }
                        // IU/dL
                        24 -> {
                            unitCalculate = input * 10 * 1000 * 0.047.toFloat()
                            unitNumber = 2
                        }
                        /** μIU/L **/
                        25 -> {
                            unitCalculate = input / 1000 * 0.047.toFloat()
                            unitNumber = 2
                        }
                        // μIU/mL
                        26 -> {
                            unitCalculate = input * 0.047.toFloat()
                            unitNumber = 2
                        }
                        // μIU/dL
                        27 -> {
                            unitCalculate = input / 100 * 0.047.toFloat()
                            unitNumber = 2
                        }

                        else -> {
                            Log.e("SqGadgetTag", "ERROR TO CONVERT")
                        }
                    }
                }
                when (unitNumber) {
                    /** pmol/L **/
                    0 -> {
                        unit001 = unitCalculate
                        unit002 = unit001 / 1000
                        unit003 = unit001 / 10
                        unit101 = unit001 * 1000 * 0.27240533914464726.toFloat()
                        unit102 = unit101 / 1000
                        unit103 = unit101 / 10
                        unit104 = unit103
                        unit201 = unit102 / 10 / 28.81844380403458.toFloat()
                        unit202 = unit201 / 1000
                        unit203 = unit201 / 10
                        unit301 = unit201 * 10 * 28.81844380403458.toFloat()
                        unit302 = unit301 / 1000
                        unit303 = unit301 / 10
                        unit304 = unit303
                        unit401 = unit302
                        unit402 = unit401 / 1000
                        unit403 = unit401 / 10
                        unit501 = unit401 / 0.047.toFloat()
                        unit502 = unit501 / 1000
                        unit503 = unit501 / 10
                        unit601 = unit502
                        unit602 = unit601 / 1000
                        unit603 = unit601 / 10
                        unit701 = unit501 * 1000
                        unit702 = unit501
                        unit703 = unit701 / 10
                        unitNumber = -1
                        resultColor = 0
                    }
                    /** nmol/L **/
                    1 -> {
                        unit201 = unitCalculate
                        unit001 =
                            unit201 * 10 * 28.81844380403458.toFloat() / 0.27240533914464726.toFloat()
                        unit002 = unit001 / 1000
                        unit003 = unit001 / 10
                        unit101 = unit001 * 1000 * 0.27240533914464726.toFloat()
                        unit102 = unit101 / 1000
                        unit103 = unit101 / 10
                        unit104 = unit103
                        unit202 = unit201 / 1000
                        unit203 = unit201 / 10
                        unit301 = unit201 * 10 * 28.81844380403458.toFloat()
                        unit302 = unit301 / 1000
                        unit303 = unit301 / 10
                        unit304 = unit303
                        unit401 = unit302
                        unit402 = unit401 / 1000
                        unit403 = unit401 / 10
                        unit501 = unit401 / 0.047.toFloat()
                        unit502 = unit501 / 1000
                        unit503 = unit501 / 10
                        unit601 = unit502
                        unit602 = unit601 / 1000
                        unit603 = unit601 / 10
                        unit701 = unit501 * 1000
                        unit702 = unit501
                        unit703 = unit701 / 10
                        unitNumber = -1
                        resultColor = 1
                    }
                    /** µg/L **/
                    2 -> {
                        unit401 = unitCalculate
                        unit001 = unit401 * 1000 / 0.27240533914464726.toFloat()
                        unit002 = unit001 / 1000
                        unit003 = unit001 / 10
                        unit101 = unit001 * 1000 * 0.27240533914464726.toFloat()
                        unit102 = unit101 / 1000
                        unit103 = unit101 / 10
                        unit104 = unit103
                        unit201 = unit401 * 100 / 28.81844380403458.toFloat()
                        unit202 = unit201 / 1000
                        unit203 = unit201 / 10
                        unit301 = unit201 * 10 * 28.81844380403458.toFloat()
                        unit302 = unit301 / 1000
                        unit303 = unit301 / 10
                        unit304 = unit303
                        unit402 = unit401 / 1000
                        unit403 = unit401 / 10
                        unit501 = unit401 / 0.047.toFloat()
                        unit502 = unit501 / 1000
                        unit503 = unit501 / 10
                        unit601 = unit502
                        unit602 = unit601 / 1000
                        unit603 = unit601 / 10
                        unit701 = unit501 * 1000
                        unit702 = unit501
                        unit703 = unit701 / 10
                        unitNumber = -1
                        resultColor = preResultColor
                    }
                }
                valueState.onCalculateClick = false
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(17.dp)
            ) {
                Text(
                    text = "Original Unit: $originalUnit",
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    textAlign = TextAlign.Justify,
                    maxLines = 1,
                    modifier = modifier.padding(start = 5.dp, top = 3.dp)
                )
            }
            SelectionContainer {
                val state = valueState.calculateState
                val color0 = if (resultColor == 0 || !state) 0 else 1
                val color1 = if (resultColor == 1 || !state) 0 else 1
                val color10 = if (resultColor == 1 || resultColor == 0 || !state) 0 else 1
                val color12 = if (resultColor == 2 || resultColor == 1 || !state) 0 else 1
                val color2 = if (resultColor == 2 || !state) 0 else 1
                val color20 = if (resultColor == 2 || resultColor == 20 || !state) 0 else 1
                Row(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                    ) {
                        TextResult(text = "pmol/L: ${unit001.toStringFix(valueState)}", color0)
                        TextResult(text = "pmol/mL: ${unit002.toStringFix(valueState)}", color0)
                        TextResult(text = "pmol/dL: ${unit003.toStringFix(valueState)}", color0)
                        TextResult(text = "pg/L: ${unit101.toStringFix(valueState)}", color0)
                        TextResult(text = "pg/mL: ${unit102.toStringFix(valueState)}", color0)
                        TextResult(text = "pg/dL: ${unit103.toStringFix(valueState)}", color0)
                        TextResult(text = "pg/%: ${unit104.toStringFix(valueState)}", color0)
                        TextResult(text = "pg/100mL: ${unit104.toStringFix(valueState)}", color0)
                        TextResult(text = "nmol/L: ${unit201.toStringFix(valueState)}", color1)
                        TextResult(text = "nmol/mL: ${unit202.toStringFix(valueState)}", color1)
                        TextResult(text = "nmol/dL: ${unit203.toStringFix(valueState)}", color1)
                        TextResult(text = "ng/L: ${unit301.toStringFix(valueState)}", color10)
                        TextResult(text = "ng/mL: ${unit302.toStringFix(valueState)}", color12)
                        TextResult(text = "ng/dL: ${unit303.toStringFix(valueState)}", color12)
                    }
                    Box(modifier = modifier.size(1.dp, cardHeight - 28.dp)) {
                        VerticalDivider(modifier.fillMaxSize())
                    }
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                    ) {
                        TextResult(text = "ng%: ${unit304.toStringFix(valueState)}", color12)
                        TextResult(text = "ng100mL: ${unit304.toStringFix(valueState)}", color12)
                        TextResult(text = "µg/L: ${unit401.toStringFix(valueState)}", color12)
                        TextResult(text = "µg/mL: ${unit402.toStringFix(valueState)}", color2)
                        TextResult(text = "µg/dL: ${unit403.toStringFix(valueState)}", color2)
                        TextResult(text = "mIU/L: ${unit501.toStringFix(valueState)}", color20)
                        TextResult(text = "mIU/mL: ${unit502.toStringFix(valueState)}", color20)
                        TextResult(text = "mIU/dL: ${unit503.toStringFix(valueState)}", color20)
                        TextResult(text = "IU/L: ${unit601.toStringFix(valueState)}", color20)
                        TextResult(text = "IU/mL: ${unit602.toStringFix(valueState)}", color20)
                        TextResult(text = "IU/dL: ${unit603.toStringFix(valueState)}", color20)
                        TextResult(text = "μIU/L: ${unit701.toStringFix(valueState)}", color20)
                        TextResult(text = "μIU/mL: ${unit702.toStringFix(valueState)}", color20)
                        TextResult(text = "μIU/dL: ${unit703.toStringFix(valueState)}", color20)
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
            val heightOnCard = when (screenHeight) {
                in 0..810 -> 12.dp
                else -> 18.dp
            }
            Spacer(modifier = modifier.height(heightOnCard))
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
                        .padding(start = 7.dp, end = 7.dp, top = 7.dp)
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
    var showConvertAlertDialog by remember { mutableStateOf(false) }
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
                    .verticalScroll(rememberScrollState())
                    .padding(7.dp)
            ) {
                var buttonOfUnit by remember { mutableIntStateOf(0) }
                val rowUnits = listOf(3, 4, 3, 4, 3, 2, 3, 3, 3)
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
                                    when (unit) {
                                        in 11..16 -> preResultColor = 2
                                        in 19..21 -> showConvertAlertDialog = true
                                        in 25..27 -> showConvertAlertDialog = true
                                    }
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
    if (showConvertAlertDialog) {
        AlertDialog(
            onDismissRequest = {
                unit = -1
                showConvertAlertDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        preResultColor = 2
                        showConvertAlertDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        preResultColor = 20
                        showConvertAlertDialog = false
                    }
                ) {
                    Text("No")
                }
            },
            title = {
                Text(
                    text = "Are you converting Prolactin (PRL) ?",
                    fontSize = 20.sp
                )
            }
        )
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
private fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

@Composable
private fun Float.toStringFix(valueState: ValueState): String {
    val numberInt = this.toString().substringAfterLast('.').substringBeforeLast('-').length
    val convert = numberInt.toString().plus('f')
    val roundIf = if (String.format("%.$convert", this).contains(""".000""".toRegex())) {
        val roundNumber = String.format("%.$convert", this).toDouble().round(5).toFloat()
        val roundInt =
            roundNumber.toString().substringAfterLast('.').substringBeforeLast('-').length
        val roundConvert = roundInt.toString().plus('f')
        String.format("%.$roundConvert", roundNumber)
    } else {
        String.format("%.$convert", this)
    }
    val output = if (!String.format("%.$convert", this).endsWith("00")) {
        roundIf
    } else if (String.format("%.$convert", this).endsWith("0.0")) {
        roundIf
    } else {
        roundIf.plus('1')
    }
    return if (valueState.calculateState) {
        output
    } else {
        "- - -"
    }
}

@Composable
private fun TextResult(text: String, color: Int, modifier: Modifier = Modifier) {
    val padding = when (LocalConfiguration.current.screenHeightDp) {
        in 0..610 -> 1.dp
        in 611..850 -> 3.dp
        else -> 4.dp
    }
    Text(
        text = text,
        fontSize = (15 - 2).sp,
        lineHeight = 11.sp,
        maxLines = 1,
        textAlign = TextAlign.Justify,
        fontWeight = FontWeight.ExtraBold,
        color = when (color) {
            0 -> Color.Unspecified
            1 -> MaterialTheme.colorScheme.secondary
            else -> Color.Unspecified
        },
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
    // preview height = 850.dp
    val valueState: ValueState = viewModel()
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HormoneUnitConversion(valueState)
            //Spacer(modifier = Modifier.fillMaxWidth().height(110.dp).align(Alignment.BottomCenter).background(Color.Cyan))
        }
    }
}