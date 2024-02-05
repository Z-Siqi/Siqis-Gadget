package com.sqz.gadget.ui.layout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.InputTransformation
import androidx.compose.foundation.text2.input.byValue
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sqz.gadget.R
import com.sqz.gadget.ValueState
import kotlin.math.pow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalculateLayout(navController: NavController, modifier: Modifier = Modifier) {
    val valueState: ValueState = viewModel()
    val focus = LocalFocusManager.current
    Surface(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { focus.clearFocus() }
            },
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = modifier.padding(10.dp)
            ) {
                Icon(painter = painterResource(R.drawable.back), contentDescription = "back")
            }
        }
        val textFieldState = rememberTextFieldState()
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = KeyboardHeight.currentDp.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = modifier
                    .align(Alignment.Start)
                    .padding(start = 50.dp),
                text = "Calculate The Area of Circle"
            )
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
                        val pi = 3.14159265358979
                        val calculate = pi * input.pow(2)
                        Text(text = "≈ $calculate")
                    } else {
                        Text(text = "A = πr^2")
                    }
                }
            }
            Spacer(modifier = modifier.height(25.dp))
            HorizontalDivider(modifier = modifier.padding(start = 16.dp, end = 16.dp))
            Spacer(modifier = modifier.height(30.dp))
            Text(
                modifier = modifier
                    .align(Alignment.Start)
                    .padding(start = 55.dp),
                text = "Unit: Radius",
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
                    }
                )
            }
            Spacer(modifier = modifier.height(25.dp))
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
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val navController = rememberNavController()
    CalculateLayout(navController)
}