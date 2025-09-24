package com.sqz.gadget.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import com.sqz.gadget.ui.common.getDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sqz.gadget.lib.CircleUnit
import sqz.gadget.lib.CircleUnit.Companion.Circle

class CircleUnitViewModel : ViewModel() {
    data class UnitState(
        val currentUnit: Circle? = null,
        val toUnit: Circle? = null,
        val calculate: Double? = null,
    )

    private val _circleUnit = CircleUnit()

    private val _state = MutableStateFlow(UnitState())
    val state: StateFlow<UnitState> = _state.asStateFlow()

    fun setCurrentUnit(unit: Circle?) {
        if (_circleUnit.getToUnit() == unit && unit != null) {
            _circleUnit.setToUnit(null)
            _circleUnit.setCurrent(unit)
            _state.update { it.copy(currentUnit = unit, toUnit = null) }
        } else {
            _circleUnit.setCurrent(unit)
            _state.update { it.copy(currentUnit = unit) }
        }
    }

    fun setToUnit(unit: Circle?) {
        if (_circleUnit.getCurrentUnit() == unit && unit != null) this.reset() else {
            _circleUnit.setToUnit(unit)
            _state.update { it.copy(toUnit = unit) }
        }
    }

    fun setCalculate(textFieldState: TextFieldState?) {
        if (textFieldState == null || textFieldState.text.isEmpty() || textFieldState.text.isBlank()) {
            _state.update { it.copy(calculate = null) }
            return
        }
        try {
            _state.update { it.copy(calculate = textFieldState.getDouble()) }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private fun reset() {
        _circleUnit.reset()
        _state.update { it.copy(currentUnit = null, toUnit = null) }
    }

    fun getFormulaString(): String? {
        return _circleUnit.getFormulaString()
    }

    fun calculate(value: Double): Double? {
        _circleUnit.setUnit(_state.value.currentUnit, _state.value.toUnit)
        return try {
            _circleUnit.calculate(value)
        } catch (_: NullPointerException) {
            null
        }
    }
}
