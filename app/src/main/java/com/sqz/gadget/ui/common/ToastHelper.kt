package com.sqz.gadget.ui.common

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

fun makeToast(text: String, context: Context, short: Boolean = true) {
    val duration = if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    Toast.makeText(context, text, duration).show()
}

@Composable
@ReadOnlyComposable
fun MakeToast(text: String, short: Boolean = true) {
    makeToast(text, context = LocalContext.current, short)
}
