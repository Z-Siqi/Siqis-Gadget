package sqz.gadget.lib.calculate

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow
import kotlin.math.sqrt

class CircleUnit {
    companion object {
        /** Length of Circle **/
        enum class Circle { Area, Diameter, Circumference, Radius }

        /** Pi value **/
        const val PI = 3.141592653589793
    }

    private var currentCircle: MutableStateFlow<Circle?>
    private var toCircle: MutableStateFlow<Circle?>

    constructor(currentCircle: Circle? = null, toCircle: Circle? = null) {
        this.currentCircle = MutableStateFlow(currentCircle)
        this.toCircle = MutableStateFlow(toCircle)
    }

    constructor() {
        this.currentCircle = MutableStateFlow(null)
        this.toCircle = MutableStateFlow(null)
    }

    fun setCurrent(currentCircle: Circle?): Circle? {
        this.checkUnits()
        this.currentCircle.update { currentCircle }
        return this.currentCircle.value
    }

    fun setToUnit(toCircle: Circle?): Circle? {
        this.checkUnits()
        this.toCircle.update { toCircle }
        return this.toCircle.value
    }

    private fun checkUnits() {
        if (this.currentCircle.value == null || this.toCircle.value == null) {
            return
        } else if (this.currentCircle == this.toCircle) {
            throw IllegalArgumentException("Current Length and To Length are the same")
        }
    }

    fun getCurrentUnit(): StateFlow<Circle?> {
        return this.currentCircle.asStateFlow()
    }

    fun getToUnit(): StateFlow<Circle?> {
        return this.toCircle.asStateFlow()
    }

    private fun fromArea(input: Double): Double {
        val calculate = when (this.toCircle.value!!) {
            Circle.Area -> throw IllegalArgumentException("Invalid Length")
            Circle.Diameter -> sqrt((4 * input) / PI) // d = √4A/π
            Circle.Circumference -> 2 * sqrt(PI * input) // C = 2√πA
            Circle.Radius -> sqrt(input / PI) // r = √A/π
        }
        return calculate
    }

    private fun fromDiameter(input: Double): Double {
        val calculate = when (this.toCircle.value!!) {
            Circle.Area -> PI * (input / 2).pow(2) // A = π(d/2)^2
            Circle.Diameter -> throw IllegalArgumentException("Invalid Length")
            Circle.Circumference -> PI * input // C = πd
            Circle.Radius -> input / 2 // r = d/2
        }
        return calculate
    }

    private fun fromCircumference(input: Double): Double {
        val calculate = when (this.toCircle.value!!) {
            Circle.Area -> (input.pow(2)) / (4 * PI) // A = (C^2)/(4π)
            Circle.Diameter -> input / PI // d = C/π
            Circle.Circumference -> throw IllegalArgumentException("Invalid Length")
            Circle.Radius -> input / (2 * PI) // r = C/2π
        }
        return calculate
    }

    private fun fromRadius(input: Double): Double {
        val calculate = when (this.toCircle.value!!) {
            Circle.Area -> PI * input.pow(2) // A = πr^2
            Circle.Diameter -> 2 * input // d = 2r
            Circle.Circumference -> 2 * PI * input // C = 2πr
            Circle.Radius -> throw IllegalArgumentException("Invalid Length")
        }
        return calculate
    }

    fun calculate(input: Double): Double {
        if (this.currentCircle.value == null || this.toCircle.value == null) {
            throw NullPointerException("Current Length or To Length is null")
        } else {
            this.checkUnits()
        }
        return when (this.currentCircle.value!!) {
            Circle.Area -> this.fromArea(input)
            Circle.Diameter -> this.fromDiameter(input)
            Circle.Circumference -> this.fromCircumference(input)
            Circle.Radius -> this.fromRadius(input)
        }
    }

    private fun formulaFromArea(toCircle: Circle): String {
        return when (toCircle) {
            Circle.Area -> throw IllegalArgumentException("Invalid Length")
            Circle.Diameter -> "d = √4A/π"
            Circle.Circumference -> "C = 2√πA"
            Circle.Radius -> "r = √A/π"
        }
    }

    private fun formulaFromDiameter(toCircle: Circle): String {
        return when (toCircle) {
            Circle.Area -> "A = π(d/2)^2"
            Circle.Diameter -> throw IllegalArgumentException("Invalid Length")
            Circle.Circumference -> "C = πd"
            Circle.Radius -> "r = d/2"
        }
    }

    private fun formulaFromCircumference(toCircle: Circle): String {
        return when (toCircle) {
            Circle.Area -> "A = C^2/4π"
            Circle.Diameter -> "d = C/π"
            Circle.Circumference -> throw IllegalArgumentException("Invalid Length")
            Circle.Radius -> "r = C/2π"
        }
    }

    private fun formulaFromRadius(toCircle: Circle): String {
        return when (toCircle) {
            Circle.Area -> "A = πr^2"
            Circle.Diameter -> "d = 2r"
            Circle.Circumference -> "C = 2πr"
            Circle.Radius -> throw IllegalArgumentException("Invalid Length")
        }
    }

    fun getFormulaString(): String? {
        if (this.currentCircle.value == null || this.toCircle.value == null) {
            return null
        }
        return when (this.currentCircle.value!!) {
            Circle.Area -> this.formulaFromArea(this.toCircle.value!!)
            Circle.Diameter -> this.formulaFromDiameter(this.toCircle.value!!)
            Circle.Circumference -> this.formulaFromCircumference(this.toCircle.value!!)
            Circle.Radius -> this.formulaFromRadius(this.toCircle.value!!)
        }
    }

    fun reset() {
        this.currentCircle.update { null }
        this.toCircle.update { null }
    }
}
