package com.sqz.gadget.viewmodel

import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sqz.gadget.lib.HormoneUnit
import sqz.gadget.lib.HormoneUnit.ConcentrationUnit.Companion.asSortedList

class HormoneUnitViewModel : ViewModel() {
    private val hormoneUnit = HormoneUnit(maxFractionDigits = 8)

    data class CurrentState(
        val strValue: String? = null,
        val unit: HormoneUnit.ConcentrationUnit? = null,
        val type: HormoneUnit.Hormone? = null
    )

    private val _currentValues: MutableStateFlow<CurrentState> = MutableStateFlow(CurrentState())
    val currentValues = _currentValues.asStateFlow()

    enum class CalculateErr {
        None, Conflict, Empty
    }

    data class CalculateState(
        val errType: CalculateErr = CalculateErr.None,
        val result: Map<HormoneUnit.ConcentrationUnit, String>? = null,
        val isLoading: Boolean = false,
        val isCalValue: Pair<HormoneUnit.ConcentrationUnit, String>? = null,
    )

    private val _calState: MutableStateFlow<CalculateState> = MutableStateFlow(CalculateState())
    val calState = _calState.asStateFlow()

    fun setHormoneValue(value: String) {
        val strValue = _currentValues.value.strValue
        if (value.isBlank() && strValue == null || value == "." || value == strValue) {
            return
        }
        _currentValues.update { it.copy(strValue = value) }
    }

    fun setHormoneType(type: HormoneUnit.Hormone) {
        _currentValues.update { it.copy(type = type) }
    }

    fun setHormoneUnit(unit: HormoneUnit.ConcentrationUnit) {
        _currentValues.update { it.copy(unit = unit) }
    }

    fun calculate() {
        val calValue = _calState.value
        if (calValue.errType != CalculateErr.None) _calState.update {
            CalculateState()
        }
        val curValue = _currentValues.value
        val strValueEmpty = curValue.strValue.isNullOrBlank()
        val emptyUnitOrType = curValue.unit == null || curValue.type == null
        if (strValueEmpty || emptyUnitOrType) {
            _calState.update { it.copy(errType = CalculateErr.Empty) }
            return
        }
        if (!hormoneUnit.isApplicable(curValue.type, curValue.unit)) {
            _calState.update { it.copy(errType = CalculateErr.Conflict) }
            return
        }
        _calState.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val isCalValueStr = hormoneUnit.let {
                val parse = it.parse(curValue.strValue)
                it.format(parse!!)
            }
            if (_currentValues.value == CurrentState()) {
                throw NullPointerException("Invalid CurrentState, convert impossible!")
            }
            val convert = this@HormoneUnitViewModel.getConvertedValue()
            _calState.update {
                it.copy(
                    result = convert,
                    isLoading = false,
                    isCalValue = curValue.unit to isCalValueStr,
                )
            }
        }
    }

    private suspend fun getConvertedValue(): Map<HormoneUnit.ConcentrationUnit, String> {
        val calculate = withContext(Dispatchers.IO) {
            val curValue = _currentValues.value
            var map: Map<HormoneUnit.ConcentrationUnit, String> = mapOf()
            val applicableUnits = hormoneUnit.applicableUnits(curValue.type!!)
                .asSortedList()
            applicableUnits.fastForEach {
                val convertedValue = hormoneUnit.convert(
                    value = hormoneUnit.parse(curValue.strValue!!)!!,
                    from = curValue.type.with(curValue.unit!!),
                    to = curValue.type.with(it)
                )
                map = map + (it to hormoneUnit.format(convertedValue))
            }
            map
        }
        return calculate
    }
}
