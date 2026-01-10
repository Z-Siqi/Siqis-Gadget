package com.sqz.gadget.ui.layout.calculate

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.gadget.ui.common.bars.*
import com.sqz.gadget.ui.common.*
import com.sqz.gadget.ui.theme.Typography
import com.sqz.gadget.viewmodel.HormoneUnitViewModel
import com.sqz.gadget.viewmodel.NavViewModel
import sqz.gadget.lib.HormoneUnit
import sqz.gadget.lib.HormoneUnit.ConcentrationUnit.Companion.asSortedList

@Composable
fun HormoneUnitLayout(
    navViewModel: NavViewModel,
    modifier: Modifier = Modifier,
    viewModel: HormoneUnitViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val screenSize = LocalWindowInfo.current.containerSize
    BackButtonTopAppBar(
        title = "Hormone Units Conversion",
        onBackClick = { navViewModel.requestBack() },
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { focusManager.clearFocus() }
        }
    ) {
        val setterHeightPx = remember {
            mutableIntStateOf((screenSize.height / 4.1).toInt())
        }
        Column(
            modifier = Modifier.verticalScrollWithFixedScrollBar(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                    setterHeightPx.intValue = layoutCoordinates.size.height
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HormoneSetter(
                    currentState = viewModel.currentValues.collectAsState().value,
                    onTextChange = viewModel::setHormoneValue,
                    onTypeSelect = viewModel::setHormoneType,
                    onUnitSelect = viewModel::setHormoneUnit,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.calculate()
                        focusManager.clearFocus()
                    },
                    content = { Text(text = "Calculate") },
                    modifier = Modifier.widthIn(min = 150.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(start = 38.dp, end = 38.dp))
                Spacer(modifier = Modifier.height(24.dp))
            }
            val navInset: Int =
                WindowInsets.navigationBars.getBottom(LocalDensity.current).pxToDpInt()
            val emptyHeight: Int =
                (screenSize.height - setterHeightPx.intValue).pxToDpInt() - 20 - navInset
            HormoneCard(
                height = (emptyHeight * 0.618).toInt().limit(min = 280, max = 580).dp,
                size = screenSize,
                calculateState = viewModel.calState.collectAsState().value
            )
            Spacer(modifier = Modifier.height(20.dp))
            HormoneTip(
                height = (emptyHeight * 0.182).toInt().limit(min = 128, max = 320).dp,
                size = screenSize,
            )
            Spacer(modifier = Modifier.height(navInset.dp))
        }
    }
}

@Composable
private fun HormoneTip(height: Dp, size: IntSize) {
    val paddingEdge = (size.width * 0.115).toInt().pxToDpInt().let {
        when {
            it > 80 -> 80
            it < 16 -> 16
            else -> it
        }
    }
    val showDialog = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = paddingEdge.dp, end = paddingEdge.dp)
            .heightIn(max = height)
            .pointerInput(Unit) {
                detectTapGestures { showDialog.value = true }
            },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier
                .horizontalScrollWithFixedScrollBar()
                .widthIn(min = (size.width.pxToDpInt() - (paddingEdge * 2)).dp)
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
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
    if (showDialog.value) AlertDialog(
        onDismissRequest = { showDialog.value = false }, confirmButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text("OK")
            }
        }, title = {
            Text("Normal Range of Hormones")
        }, text = {
            val description = listOf(
                "Estradiol (E2): 30 to 400 pg/mL for premenopausal female; 0 to 30 pg/mL for postmenopausal female. 10 to 50 pg/mL for male.",
                "Testosterone (T): 300 to 1,000 ng/dL for male. 15 to 70 ng/dL for female.",
                "Progesterone (P): 0.1 to 0.7 ng/mL in the follicular stage of the menstrual cycle; 2 to 25 ng/mL in the luteal stage of the menstrual cycle; 10 to 290 ng/mL in pregnancy. 0.13–0.97 ng/mL for male.",
                "Luteinizing Hormone (LH): Female, follicular phase of menstrual cycle: 1.68 to 15 IU/L; Female, midcycle peak: 21.9 to 56.6 IU/L. 1.24 to 7.8 IU/L for male.",
                "Follicle-Stimulating Hormone (FSH): 4.7 to 21.5 mIU/mL for female; 25.8 to 134.8 mIU/mL for postmenopausal female. 1.5 to 12.4 mIU/mL for male.",
                "Prolactin (PRL): less than 25 ng/mL for female; 80 to 400 ng/mL for pregnant female. less than 20 ng/mL for male.",
            )
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                description.fastForEachIndexed { index, str ->
                    if (index != 0) {
                        HorizontalDivider()
                    }
                    Text(text = str)
                }
            }
        }
    )
}

@Composable
private fun TextOfNote(
    modifier: Modifier = Modifier,
    text: String,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Justify,
) = Text(
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HormoneCard(
    height: Dp,
    size: IntSize,
    calculateState: HormoneUnitViewModel.CalculateState,
) {
    val paddingEdge = (size.width * 0.115).toInt().pxToDpInt().let {
        when {
            it > 80 -> 80
            it < 16 -> 16
            else -> it
        }
    }
    Card(
        modifier = Modifier
            .height(height)
            .fillMaxSize()
            .padding(start = paddingEdge.dp, end = paddingEdge.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollWithFixedScrollBar()
                .padding(4.dp)
                .padding(start = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (calculateState.errType) {
                HormoneUnitViewModel.CalculateErr.None -> {
                    when {
                        calculateState.isLoading -> LoadingIndicator()

                        calculateState.result != null && calculateState.isCalValue != null -> {
                            HormoneResult(
                                isCalValue = calculateState.isCalValue,
                                result = calculateState.result
                            )
                        }

                        else -> HormoneUnit.ConcentrationUnit.entries.asSortedList().fastForEach {
                            Text(
                                text = "$it: - - -",
                                modifier = Modifier.align(Alignment.Start),
                                style = Typography.titleSmall,
                            )
                        }
                    }
                }

                HormoneUnitViewModel.CalculateErr.Conflict -> {
                    Text(
                        text = "Hormone type is conflict with unit! Please check the validity",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                        style = Typography.titleLarge
                    )
                }

                HormoneUnitViewModel.CalculateErr.Empty -> {
                    Text(
                        text = "Please input hormone value, select type & unit to convert",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = Typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun HormoneResult(
    isCalValue: Pair<HormoneUnit.ConcentrationUnit, String>,
    result: Map<HormoneUnit.ConcentrationUnit, String>
) = SelectionContainer {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
    ) {
        isCalValue.let { (unit, value) ->
            Text(
                text = "Original: $value $unit",
                fontSize = 12.sp,
                lineHeight = 12.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier.align(Alignment.End)
            )
        }
        result.entries.forEach { (unit, result) ->
            Text(
                text = "$unit: $result",
                modifier = Modifier.align(Alignment.Start),
                style = Typography.titleSmall,
            )
        }
    }
}

@Composable
private fun ColumnScope.HormoneSetter(
    currentState: HormoneUnitViewModel.CurrentState,
    onTextChange: (String) -> Unit,
    onTypeSelect: (HormoneUnit.Hormone) -> Unit,
    onUnitSelect: (HormoneUnit.ConcentrationUnit) -> Unit,
) {
    val size = LocalWindowInfo.current.containerSize
    val paddingEdge = (size.width * 0.105).toInt().pxToDpInt().let {
        when {
            it > 50 -> 50
            it < 16 -> 16
            else -> it
        }
    }
    val focusRequester = remember { FocusRequester() }
    val expandedType = remember { mutableStateOf(false) }
    val expandedUnit = remember { mutableStateOf(false) }
    Text(
        modifier = Modifier
            .align(Alignment.Start)
            .padding(start = (paddingEdge - 3).dp),
        fontSize = 16.sp,
        text = "Hormone to be convert:",
        fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedCard(
        modifier = Modifier
            .requiredWidthIn(min = 220.dp)
            .fillMaxWidth()
            .height(52.dp)
            .padding(start = paddingEdge.dp, end = paddingEdge.dp)
            .pointerInput(Unit) {
                detectTapGestures { focusRequester.requestFocus() }
            },
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        val textFieldState = rememberTextFieldState()
        Spacer(modifier = Modifier.weight(1f))
        BasicTextFieldForDouble(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .focusRequester(focusRequester),
            textFieldState = textFieldState
        )
        LaunchedEffect(textFieldState.text) {
            onTextChange(textFieldState.text.toString())
        }
        Spacer(modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(8.dp))
    BoxWithConstraints(
        modifier = Modifier.padding(start = paddingEdge.dp, end = paddingEdge.dp)
    ) {
        val goldenWidth = maxWidth / 1.618.dp
        val typeWidth = goldenWidth - 4
        val valueWidth = maxWidth.value - goldenWidth - 4
        Row {
            FilterChip(
                selected = false,
                onClick = { expandedType.value = !expandedType.value }, label = {
                    Text(
                        text = currentState.type.toStr(),
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 3.sp, maxFontSize = LocalTextStyle.current.fontSize
                        ),
                    )
                }, modifier = Modifier
                    .width(typeWidth.dp)
                    .heightIn(min = 42.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )
            HormoneTypeMenu(
                expanded = expandedType,
                onSelect = onTypeSelect,
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = false,
                onClick = { expandedUnit.value = !expandedUnit.value }, label = {
                    Text(
                        text = currentState.unit?.toString() ?: "Unit",
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = 3.sp, maxFontSize = LocalTextStyle.current.fontSize
                        ),
                    )
                }, modifier = Modifier
                    .width(valueWidth.dp)
                    .heightIn(min = 42.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null
                    )
                }
            )
            HormoneUnitSheet(
                expanded = expandedUnit,
                onSelect = onUnitSelect,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HormoneTypeMenu(
    expanded: MutableState<Boolean>,
    onSelect: (HormoneUnit.Hormone) -> Unit
) {
    val focusManager = LocalFocusManager.current
    DropdownMenuPopup(
        modifier = Modifier.clip(ShapeDefaults.Large),
        expanded = expanded.value,
        onDismissRequest = {
            expanded.value = false
            focusManager.clearFocus()
        }
    ) {
        DropdownMenuGroup(shapes = MenuDefaults.groupShapes()) {
            HormoneUnit.Hormone.entries.fastForEach { item ->
                DropdownMenuItem(
                    text = { Text(item.toStr()) }, onClick = {
                        focusManager.clearFocus()
                        onSelect(item)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HormoneUnitSheet(
    expanded: MutableState<Boolean>,
    onSelect: (HormoneUnit.ConcentrationUnit) -> Unit
) {
    val unitGroups = remember {
        HormoneUnit.ConcentrationUnit.entries
            .groupBy { it.toString().prefixBeforeAny('/', '%') }
            .values
            .toList()
    }
    if (expanded.value) ModalBottomSheet(
        onDismissRequest = { expanded.value = false }
    ) {
        Column(
            modifier = Modifier
                .verticalScrollWithFixedScrollBar()
                .padding(bottom = 32.dp)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            unitGroups.forEach { group ->
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    group.forEach { unit ->
                        AssistChip(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            onClick = {
                                onSelect(unit)
                                expanded.value = false
                            }, label = {
                                Text(
                                    text = unit.toString(),
                                    maxLines = 1,
                                    autoSize = TextAutoSize.StepBased(
                                        minFontSize = 8.sp,
                                        maxFontSize = LocalTextStyle.current.fontSize
                                    ),
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Suppress("SameParameterValue")
private fun String.prefixBeforeAny(vararg sep: Char): String {
    val idx = sep
        .map { this.indexOf(it) }
        .filter { it >= 0 }
        .minOrNull()
    return if (idx == null) this else this.substring(0, idx)
}

private fun Int.limit(min: Int, max: Int): Int {
    return when {
        this > max -> max
        this < min -> min
        else -> this
    }
}

@Composable
private fun HormoneUnit.Hormone?.toStr(): String {
    return when (this) {
        HormoneUnit.Hormone.Estradiol -> "Estradiol (E2)"
        HormoneUnit.Hormone.Testosterone -> "Testosterone (T)"
        HormoneUnit.Hormone.Progesterone -> "Progesterone (P)"
        HormoneUnit.Hormone.Luteinizing -> "Luteinizing Hormone (LH)"
        HormoneUnit.Hormone.FollicleStimulating -> "Follicle-Stimulating Hormone (FSH)"
        HormoneUnit.Hormone.Prolactin -> "Prolactin (PRL)"
        null -> "Hormone Type"
    }
}

@Preview
@Composable
private fun Preview() {
    HormoneUnitLayout(navViewModel = viewModel())
}
