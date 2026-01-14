package com.sqz.gadget.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import com.sqz.gadget.ui.common.getDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sqz.gadget.lib.calculate.CircleUnit
import sqz.gadget.lib.calculate.CircleUnit.Companion.Circle

class CircleUnitViewModel : ViewModel() {

    private val _circleUnit = CircleUnit()

    val currentUnit: StateFlow<Circle?> = _circleUnit.getCurrentUnit()
    val toUnit: StateFlow<Circle?> = _circleUnit.getToUnit()

    private val _calculate: MutableStateFlow<Double?> = MutableStateFlow(null)
    val calculate: StateFlow<Double?> = _calculate.asStateFlow()

    fun setCurrentUnit(unit: Circle?) {
        if (_circleUnit.getToUnit().value == unit && unit != null) {
            _circleUnit.setToUnit(null)
            _circleUnit.setCurrent(unit)
        } else {
            _circleUnit.setCurrent(unit)
        }
    }

    fun setToUnit(unit: Circle?) {
        if (_circleUnit.getCurrentUnit().value == unit && unit != null) this.reset() else {
            _circleUnit.setToUnit(unit)
        }
    }

    fun setCalculate(textFieldState: TextFieldState?) {
        if (textFieldState == null || textFieldState.text.isEmpty() || textFieldState.text.isBlank()) {
            _calculate.update { null }
            return
        }
        try {
            _calculate.update { textFieldState.getDouble() }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private fun reset() {
        _circleUnit.reset()
    }

    fun getFormulaString(): String? {
        return _circleUnit.getFormulaString()
    }

    fun calculate(value: Double): Double? {
        return try {
            _circleUnit.calculate(value)
        } catch (_: NullPointerException) {
            null
        }
    }
}
