package sqz.gadget.lib

import kotlin.math.pow
import kotlin.math.sqrt

class CircleUnit {
    companion object {
        /** Unit of Circle **/
        enum class Circle { Area, Diameter, Circumference, Radius }

        /** Pi value **/
        const val PI = 3.141592653589793
    }

    private var currentCircle: Circle?
    private var toCircle: Circle?

    constructor(currentCircle: Circle? = null, toCircle: Circle? = null) {
        this.currentCircle = currentCircle
        this.toCircle = toCircle
    }

    constructor() {
        this.currentCircle = null
        this.toCircle = null
    }

    fun setCurrent(currentCircle: Circle?): Circle? {
        this.checkUnits()
        this.currentCircle = currentCircle
        return this.currentCircle
    }

    fun setToUnit(toCircle: Circle?): Circle? {
        this.checkUnits()
        this.toCircle = toCircle
        return this.toCircle
    }

    fun setUnit(currentCircle: Circle?, toCircle: Circle?) {
        this.checkUnits()
        this.setCurrent(currentCircle)
        this.setToUnit(toCircle)
    }

    private fun checkUnits() {
        if (this.currentCircle == null || this.toCircle == null) {
            return
        } else if (this.currentCircle == this.toCircle) {
            throw IllegalArgumentException("Current Unit and To Unit are the same")
        }
    }

    fun getCurrentUnit(): Circle? {
        return this.currentCircle
    }

    fun getToUnit(): Circle? {
        return this.toCircle
    }

    private fun fromArea(input: Double): Double {
        val calculate = when (this.toCircle!!) {
            Circle.Area -> throw IllegalArgumentException("Invalid Unit")
            Circle.Diameter -> sqrt((4 * input) / PI) // d = √4A/π
            Circle.Circumference -> 2 * sqrt(PI * input) // C = 2√πA
            Circle.Radius -> sqrt(input / PI) // r = √A/π
        }
        return calculate
    }

    private fun fromDiameter(input: Double): Double {
        val calculate = when (this.toCircle!!) {
            Circle.Area -> PI * (input / 2).pow(2) // A = π(d/2)^2
            Circle.Diameter -> throw IllegalArgumentException("Invalid Unit")
            Circle.Circumference -> PI * input // C = πd
            Circle.Radius -> input / 2 // r = d/2
        }
        return calculate
    }

    private fun fromCircumference(input: Double): Double {
        val calculate = when (this.toCircle!!) {
            Circle.Area -> (input.pow(2)) / (4 * PI) // A = (C^2)/(4π)
            Circle.Diameter -> input / PI // d = C/π
            Circle.Circumference -> throw IllegalArgumentException("Invalid Unit")
            Circle.Radius -> input / (2 * PI) // r = C/2π
        }
        return calculate
    }

    private fun fromRadius(input: Double): Double {
        val calculate = when (this.toCircle!!) {
            Circle.Area -> PI * input.pow(2) // A = πr^2
            Circle.Diameter -> 2 * input // d = 2r
            Circle.Circumference -> 2 * PI * input // C = 2πr
            Circle.Radius -> throw IllegalArgumentException("Invalid Unit")
        }
        return calculate
    }

    fun calculate(input: Double): Double {
        if (this.currentCircle == null || this.toCircle == null) {
            throw NullPointerException("Current Unit or To Unit is null")
        } else {
            this.checkUnits()
        }
        return when (this.currentCircle!!) {
            Circle.Area -> this.fromArea(input)
            Circle.Diameter -> this.fromDiameter(input)
            Circle.Circumference -> this.fromCircumference(input)
            Circle.Radius -> this.fromRadius(input)
        }
    }

    private fun formulaFromArea(toCircle: Circle): String {
        return when (toCircle) {
            Circle.Area -> throw IllegalArgumentException("Invalid Unit")
            Circle.Diameter -> "d = √4A/π"
            Circle.Circumference -> "C = 2√πA"
            Circle.Radius -> "r = √A/π"
        }
    }

    private fun formulaFromDiameter(toCircle: Circle): String {
        return when (toCircle) {
            Circle.Area -> "A = π(d/2)^2"
            Circle.Diameter -> throw IllegalArgumentException("Invalid Unit")
            Circle.Circumference -> "C = πd"
            Circle.Radius -> "r = d/2"
        }
    }

    private fun formulaFromCircumference(toCircle: Circle): String {
        return when (toCircle) {
            Circle.Area -> "A = C^2/4π"
            Circle.Diameter -> "d = C/π"
            Circle.Circumference -> throw IllegalArgumentException("Invalid Unit")
            Circle.Radius -> "r = C/2π"
        }
    }

    private fun formulaFromRadius(toCircle: Circle): String {
        return when (toCircle) {
            Circle.Area -> "A = πr^2"
            Circle.Diameter -> "d = 2r"
            Circle.Circumference -> "C = 2πr"
            Circle.Radius -> throw IllegalArgumentException("Invalid Unit")
        }
    }

    fun getFormulaString(): String? {
        if (this.currentCircle == null || this.toCircle == null) {
            return null
        }
        return when (this.currentCircle!!) {
            Circle.Area -> this.formulaFromArea(this.toCircle!!)
            Circle.Diameter -> this.formulaFromDiameter(this.toCircle!!)
            Circle.Circumference -> this.formulaFromCircumference(this.toCircle!!)
            Circle.Radius -> this.formulaFromRadius(this.toCircle!!)
        }
    }

    fun reset() {
        this.currentCircle = null
        this.toCircle = null
    }
}
