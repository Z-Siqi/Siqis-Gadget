package com.sqz.gadget.ui.layout.calculate

import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sqz.gadget.R

@Composable
fun CalculateLayout(valueState: ValueState, navController: NavController, modifier: Modifier = Modifier) {
    val focus = LocalFocusManager.current
    Surface(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { focus.clearFocus() }
            },
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Box {
            when (valueState.calculateMode) {
                "circle" -> Circle(valueState)
                else -> Log.e("SqGadgetTag", "Error to load calculate function")
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
                    Icon(painter = painterResource(R.drawable.back), contentDescription = "back")
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