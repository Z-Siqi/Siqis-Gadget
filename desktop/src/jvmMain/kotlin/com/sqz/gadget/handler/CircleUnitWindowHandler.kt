package com.sqz.gadget.handler

import androidx.compose.foundation.text.input.TextFieldState
import com.sqz.gadget.runtime.InstanceManager
import com.sqz.gadget.ui.common.getDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sqz.gadget.lib.CircleUnit
import sqz.gadget.lib.CircleUnit.Companion.Circle

class CircleUnitWindowHandler(key: String? = null) : InstanceManager(key) {

    private val _toUnit: MutableStateFlow<Circle?> = MutableStateFlow(null)
    val toUnit: StateFlow<Circle?> = _toUnit.asStateFlow()

    private val _currentUnit: MutableStateFlow<Circle?> = MutableStateFlow(null)
    val currentUnit: StateFlow<Circle?> = _currentUnit.asStateFlow()

    private val _circleUnit = CircleUnit(_currentUnit.value, _toUnit.value)

    fun setCurrent(currentCircle: Circle?) {
        if (_circleUnit.getToUnit() == currentCircle && currentCircle != null) {
            _toUnit.update { _circleUnit.setToUnit(null) }
            _currentUnit.update { _circleUnit.setCurrent(currentCircle) }
        } else {
            _currentUnit.update { _circleUnit.setCurrent(currentCircle) }
        }
    }

    fun setToUnit(toCircle: Circle?) {
        if (_circleUnit.getCurrentUnit() == toCircle && toCircle != null) this.reset() else {
            _toUnit.update { _circleUnit.setToUnit(toCircle) }
        }
    }

    private fun reset() {
        _circleUnit.reset()
        _currentUnit.update { null }
        _toUnit.update { null }
    }

    private val _calculateValue: MutableStateFlow<Double?> = MutableStateFlow(null)
    val calculateValue: StateFlow<Double?> = _calculateValue.asStateFlow()

    fun setCalculateValue(textFieldState: TextFieldState?) {
        if (textFieldState == null || textFieldState.text.isEmpty() || textFieldState.text.isBlank()) {
            _calculateValue.update { null }
            return
        }
        try {
            _calculateValue.update { textFieldState.getDouble() }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    fun calculate(): Double? {
        return try {
            _circleUnit.calculate(_calculateValue.value!!)
        } catch (_: NullPointerException) {
            null
        }
    }

    fun getFormulaString(): String? {
        return _circleUnit.getFormulaString()
    }
}
