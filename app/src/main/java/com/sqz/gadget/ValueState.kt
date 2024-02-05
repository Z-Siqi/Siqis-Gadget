package com.sqz.gadget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ValueState : ViewModel() {
    /*value*/
    var onCalculateClick by mutableStateOf(false)
    var calculateState by mutableStateOf(false)
}

