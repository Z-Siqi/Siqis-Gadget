package sqz.gadget.lib

import java.math.RoundingMode

abstract class UnitConverter(
    protected val maxFractionDigits: Int = 8,
    protected val rounding: RoundingMode = RoundingMode.HALF_UP
) {

    /**
     * Perform numerical conversions
     * (Double calculation, internally using BigDecimal to maintain accuracy).
     */
    abstract fun convert(value: Double, from: Any, to: Any): Double

    /**
     * Result formatting: maximum maxFractionDigits decimal places, remove trailing zeros; avoid
     * scientific notation.
     */
    fun format(value: Double): String {
        val bd = value.toBigDecimal()
            .setScale(maxFractionDigits, rounding)
            .stripTrailingZeros()
        return bd.toPlainString()
    }

    /**
     * Attempt to parse the input text as Double; return null if unsuccessful.
     */
    fun parse(input: String?): Double? {
        if (input.isNullOrBlank()) return null
        return input.toDoubleOrNull()
    }
}
