package com.sqz.gadget.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sqz.gadget.lib.LengthUnit

class LengthUnitViewModel : ViewModel() {
    val lengthUnit = LengthUnit(maxFractionDigits = 8)

    enum class SelectedState { Top, Bottom }

    data class Status(
        val curSelectState: SelectedState? = null,
        val topValue: String? = null,
        val bottomValue: String? = null,
        val topUnit: LengthUnit.Length? = null,
        val bottomUnit: LengthUnit.Length? = null,
    )

    private val _currentState = MutableStateFlow<Status>(Status())
    val currentState = _currentState.asStateFlow()

    private fun convert(from: SelectedState) {
        if (_currentState.value.topUnit == null || _currentState.value.bottomUnit == null) {
            throw IllegalArgumentException("Both unit must set!")
        }
        val topValue = lengthUnit.parse(_currentState.value.topValue)
        val bottomValue = lengthUnit.parse(_currentState.value.bottomValue)
        if (topValue == null || bottomValue == null) {
            _currentState.value.let {
                if (it.topValue.isNullOrBlank() && it.bottomValue.isNullOrBlank() ||
                    topValue == null && !it.topValue.isNullOrEmpty() ||
                    bottomValue == null && !it.bottomValue.isNullOrEmpty()
                ) {
                    throw IllegalArgumentException("Both value must input!")
                }
            }
        }
        val topUnit = _currentState.value.topUnit!!
        val bottomUnit = _currentState.value.bottomUnit!!
        try {
            when (from) {
                SelectedState.Top -> {
                    val convert = lengthUnit.convert(topValue!!, topUnit, bottomUnit)
                    _currentState.update { it.copy(bottomValue = lengthUnit.format(convert)) }
                }

                SelectedState.Bottom -> {
                    val convert = lengthUnit.convert(bottomValue!!, bottomUnit, topUnit)
                    _currentState.update { it.copy(topValue = lengthUnit.format(convert)) }
                }
            }
        } catch (_: NullPointerException) {
            _currentState.update { it.copy(topValue = "", bottomValue = "") }
        }
    }

    fun setTopValue(setter: String) {
        if (setter.isEmpty() && _currentState.value.topValue == null) {
            return // do not set when init state
        }
        _currentState.update {
            it.copy(curSelectState = SelectedState.Top, topValue = setter)
        }
        try {
            _currentState.value.curSelectState?.let { this.convert(it) }
        } catch (e: Exception) {
            if (e !is IllegalArgumentException) throw e
        }
    }

    fun setBottomValue(setter: String) {
        if (setter.isEmpty() && _currentState.value.bottomValue == null) {
            return // do not set when init state
        }
        _currentState.update {
            it.copy(curSelectState = SelectedState.Bottom, bottomValue = setter)
        }
        try {
            _currentState.value.curSelectState?.let { this.convert(it) }
        } catch (e: Exception) {
            if (e !is IllegalArgumentException) throw e
        }
    }

    fun setConvertUnit(unit: LengthUnit.Length, from: SelectedState) {
        when (from) {
            SelectedState.Top -> _currentState.update { it.copy(topUnit = unit) }
            SelectedState.Bottom -> _currentState.update { it.copy(bottomUnit = unit) }
        }
        try {
            _currentState.value.curSelectState?.let { this.convert(it) }
        } catch (e: Exception) {
            if (e !is IllegalArgumentException) throw e
        }
    }
}
