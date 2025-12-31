package com.sqz.gadget.ui.layout.calculate

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.gadget.ui.common.BasicTextFieldForDouble
import com.sqz.gadget.ui.common.bars.BackButtonTopAppBar
import com.sqz.gadget.ui.common.bars.verticalColumnScrollbar
import com.sqz.gadget.ui.common.pxToDpInt
import com.sqz.gadget.viewmodel.LengthUnitViewModel
import com.sqz.gadget.viewmodel.LengthUnitViewModel.SelectedState
import com.sqz.gadget.viewmodel.NavViewModel
import sqz.gadget.lib.LengthUnit

@Composable
fun LengthUnitLayout(
    navViewModel: NavViewModel,
    modifier: Modifier = Modifier,
    viewModel: LengthUnitViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    BackButtonTopAppBar(
        title = "Length Unit Conversion",
        onBackClick = { navViewModel.requestBack() },
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { focusManager.clearFocus() }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LengthUnitConvertCard(viewModel = viewModel)
        }
    }
}

@Composable
private fun LengthUnitConvertCard(
    viewModel: LengthUnitViewModel,
    modifier: Modifier = Modifier
) = Card(
    modifier = modifier
        .padding(16.dp)
        .sizeIn(
            minWidth = 280.dp, minHeight = 210.dp,
            maxWidth = 1200.dp, maxHeight = 1000.dp,
        ),
    shape = ShapeDefaults.Large,
) {
    val currentStatus by viewModel.currentState.collectAsState()
    Column(
        modifier = Modifier.padding(
            start = 22.dp,
            end = 22.dp,
            top = 20.dp,
            bottom = 20.dp
        ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputCard(
            isFocus = currentStatus.curSelectState == SelectedState.Top,
            inputValue = currentStatus.topValue,
            lengthUnit = currentStatus.topUnit,
            onInput = viewModel::setTopValue,
            onUnitSet = { viewModel.setConvertUnit(it, SelectedState.Top) }
        )
        Spacer(modifier = Modifier.heightIn(max = 16.dp) then Modifier.fillMaxHeight())
        HorizontalDivider(Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp))
        Spacer(modifier = Modifier.heightIn(max = 12.dp) then Modifier.fillMaxHeight())
        InputCard(
            isFocus = currentStatus.curSelectState == SelectedState.Bottom,
            inputValue = currentStatus.bottomValue,
            lengthUnit = currentStatus.bottomUnit,
            onInput = viewModel::setBottomValue,
            onUnitSet = { viewModel.setConvertUnit(it, SelectedState.Bottom) }
        )
    }
}

@Composable
private fun InputCard(
    isFocus: Boolean,
    inputValue: String?,
    lengthUnit: LengthUnit.Length?,
    onInput: (String) -> Unit,
    onUnitSet: (LengthUnit.Length) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (isFocus) MaterialTheme.colorScheme.tertiary else Color.Transparent,
                shape = ShapeDefaults.Medium
            )
            .padding(start = 8.dp, end = 8.dp, top = 3.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 8.dp),
            text = if (isFocus) "Used unit for conversion" else "",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            modifier = Modifier.align(Alignment.Start),
            fontSize = 16.sp,
            text = "Conversion Length",
            fontWeight = FontWeight.Medium
        )
        OutlinedCard(
            modifier = Modifier
                .requiredWidthIn(min = 100.dp)
                .fillMaxWidth()
                .height(55.dp),
            shape = CircleShape,
        ) {
            val textFieldState = rememberTextFieldState()
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicTextFieldForDouble(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 4.dp)
                        .weight(1f),
                    textFieldState = textFieldState
                )
                OutlinedCard(
                    modifier = Modifier
                        .widthIn(min = 80.dp, max = 120.dp)
                        .height(32.dp)
                        .padding(end = 16.dp)
                        .width(IntrinsicSize.Min),
                    shape = ShapeDefaults.Small,
                    onClick = { expanded.value = !expanded.value }
                ) {
                    Text(
                        text = lengthUnit?.toStr() ?: "Length",
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxSize(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    LengthMenu(
                        expanded = expanded,
                        onSelect = onUnitSet,
                    )
                }
            }
            val rememberInput = remember { mutableStateOf(textFieldState.text.toString()) }
            LaunchedEffect(textFieldState.text, inputValue) {
                textFieldState.text.toString().let { text ->
                    if (text != rememberInput.value) {
                        onInput(text)
                        rememberInput.value = text
                    } else if (inputValue != null && inputValue != text) textFieldState.edit {
                        delete(0, textFieldState.text.length)
                        insert(0, inputValue)
                        rememberInput.value = inputValue
                    }
                }
            }
        }
        Spacer(modifier = Modifier.heightIn(max = 4.dp) then Modifier.fillMaxHeight())
    }
}

private fun LengthUnit.Length.toStr(): String {
    return when (this) {
        LengthUnit.Length.PM -> "pm"
        LengthUnit.Length.NM -> "nm"
        LengthUnit.Length.UM -> "um"
        LengthUnit.Length.MM -> "mm"
        LengthUnit.Length.CM -> "cm"
        LengthUnit.Length.DM -> "dm"
        LengthUnit.Length.M -> "m"
        LengthUnit.Length.KM -> "km"
        LengthUnit.Length.LY -> "ly"
        LengthUnit.Length.INCH -> "in"
        LengthUnit.Length.FOOT -> "ft"
        LengthUnit.Length.YARD -> "yd"
        LengthUnit.Length.MILE -> "mi"
        LengthUnit.Length.NMI -> "nmi"
        LengthUnit.Length.LI -> "li"
        LengthUnit.Length.FEN -> "hun"
        LengthUnit.Length.CUN -> "cun"
        LengthUnit.Length.CHI -> "chi"
        LengthUnit.Length.ZHANG -> "zhang"
        LengthUnit.Length.CHINESE_MILE -> "ri"
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LengthMenu(
    expanded: MutableState<Boolean>,
    onSelect: (LengthUnit.Length) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val lengthGroup = LengthUnit.Length.entries.fold(
        mutableListOf<MutableList<LengthUnit.Length>>()
    ) { fold, lengthUnit ->
        val lengthSet = setOf(LengthUnit.Length.PM, LengthUnit.Length.INCH, LengthUnit.Length.LI)
        if (lengthUnit in lengthSet) {
            fold.add(mutableListOf(lengthUnit))
        } else {
            fold.last().add(lengthUnit)
        }
        fold
    }
    val groupInteractionSource = remember { MutableInteractionSource() }
    val scrollState = rememberScrollState()
    val height = LocalWindowInfo.current.containerSize.height.pxToDpInt().let {
        if (it / 1.5 < 380) 380 else (it / 1.5).toInt()
    }
    DropdownMenuPopup(
        modifier = Modifier
            .height(height.dp)
            .clip(ShapeDefaults.Large),
        expanded = expanded.value,
        onDismissRequest = {
            expanded.value = false
            focusManager.clearFocus()
        }
    ) {
        val scrollModifier = Modifier.verticalColumnScrollbar(
            scrollState = scrollState,
            showScrollBar = scrollState.canScrollBackward || scrollState.canScrollForward,
            scrollBarTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            scrollBarColor = MaterialTheme.colorScheme.secondary,
        ) then Modifier.verticalScroll(scrollState)
        Column(
            modifier = scrollModifier.padding(5.dp)
        ) {
            val groupCount = lengthGroup.size
            lengthGroup.fastForEachIndexed { groupIndex, group ->
                DropdownMenuGroup(
                    shapes = MenuDefaults.groupShape(groupIndex, groupCount),
                    interactionSource = groupInteractionSource
                ) {
                    @Composable
                    fun GroupLabel(text: String) {
                        MenuDefaults.Label { Text(text) }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = MenuDefaults.HorizontalDividerPadding)
                        )
                    }
                    when {
                        LengthUnit.Length.PM in group -> GroupLabel("SI Units")
                        LengthUnit.Length.INCH in group -> GroupLabel("Imperial Units")
                        LengthUnit.Length.LI in group -> GroupLabel("Chinese Units")
                    }
                    group.fastForEach {
                        DropdownMenuItem(
                            text = { Text(it.toFullStr()) }, onClick = {
                                focusManager.clearFocus()
                                onSelect(it)
                                expanded.value = false
                            }
                        )
                    }
                }
                if (groupIndex != groupCount - 1) {
                    Spacer(Modifier.height(MenuDefaults.GroupSpacing))
                }
            }
        }
    }
}

@Composable
private fun LengthUnit.Length.toFullStr(): String {
    return when (this) {
        LengthUnit.Length.PM -> "Picometre (pm)"
        LengthUnit.Length.NM -> "Nanometre (nm)"
        LengthUnit.Length.UM -> "Micrometre (um)"
        LengthUnit.Length.MM -> "Millimeter (mm)"
        LengthUnit.Length.CM -> "Centimetre (cm)"
        LengthUnit.Length.DM -> "Decimetre (dm)"
        LengthUnit.Length.M -> "Metre (m)"
        LengthUnit.Length.KM -> "Kilometre (km)"
        LengthUnit.Length.LY -> "Light-Year (ly)"
        LengthUnit.Length.INCH -> "Inch (in)"
        LengthUnit.Length.FOOT -> "Foot (ft)"
        LengthUnit.Length.YARD -> "Yard (yd)"
        LengthUnit.Length.MILE -> "Mile (mi)"
        LengthUnit.Length.NMI -> "Nautical mile (nmi)"
        LengthUnit.Length.LI -> "Li (li)"
        LengthUnit.Length.FEN -> "Fen (hun)"
        LengthUnit.Length.CUN -> "Chinese inch (cun)"
        LengthUnit.Length.CHI -> "Chinese foot (chi)"
        LengthUnit.Length.ZHANG -> "Chinese feet (zhang)"
        LengthUnit.Length.CHINESE_MILE -> "Chinese mile (ri)"
    }
}

@Preview
@Composable
private fun Preview() {
    LengthUnitLayout(navViewModel = viewModel())
}
