package com.sqz.gadget.ui.layout.calculate

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sqz.gadget.R

@Deprecated("")
@Composable
fun CalculateLayout(
    valueState: ValueState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val focus = LocalFocusManager.current
    var calculateMode by remember { mutableStateOf("") }
    calculateMode = valueState.calculateMode
    Surface(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { focus.clearFocus() }
            },
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Box {
            when (calculateMode) {
                "hormone_units_conversion" -> HormoneUnitConversion(valueState)
                "unit_of_length" -> LengthUnitConversion()
                else -> navController.popBackStack()
            }
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = modifier.padding(10.dp)
                ) {
                    Icon(painterResource(R.drawable.back), stringResource(R.string.back))
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val navController = rememberNavController()
    val valueState: ValueState = viewModel()
    CalculateLayout(valueState, navController)
}