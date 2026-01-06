package sqz.gadget.lib

import java.math.BigDecimal
import java.math.RoundingMode

class LengthUnit : UnitConverter {
    constructor(maxFractionDigits: Int = 8) : super(maxFractionDigits = maxFractionDigits)

    enum class Length {
        // International System of Units
        PM, NM, UM, MM, CM, DM, M, KM, LY,

        // Imperial Units
        INCH, FOOT, YARD, MILE, NMI,

        // Chinese Units
        LI, FEN, CUN, CHI, ZHANG, CHINESE_MILE
    }

    // Multiplier of each unit to meter
    private val toMeterFactor: Map<Length, BigDecimal> = mapOf(
        // ===== SI Units =====
        Length.PM to BigDecimal("0.000000000001"), // 1 Picometre = 0.000000000001 m
        Length.NM to BigDecimal("0.000000001"), // 1 Nanometre = 0.000000001 m
        Length.UM to BigDecimal("0.000001"), // 1 Micrometre = 0.000001 m
        Length.MM to BigDecimal("0.001"), // 1 mm = 0.001 m
        Length.CM to BigDecimal("0.01"), // 1 cm = 0.01 m
        Length.DM to BigDecimal("0.1"), // 1 Decimetre = 0.1 m
        Length.M to BigDecimal("1"), // 1 m = 1 m
        Length.KM to BigDecimal("1000"), // 1 km = 1000 m
        Length.LY to BigDecimal("9460730472580800"), // 1 light-year ≈ 9.4607304725808e15 m

        // ===== Imperial / US Customary =====
        Length.INCH to BigDecimal("0.0254"), // 1 inch = 0.0254 m
        Length.FOOT to BigDecimal("0.3048"), // 1 foot = 0.3048 m
        Length.YARD to BigDecimal("0.9144"), // 1 yard = 0.9144 m
        Length.MILE to BigDecimal("1609.344"), // 1 statute mile = 1609.344 m
        Length.NMI to BigDecimal("1852"), // 1 nautical mile = 1852 m

        // ===== Chinese Traditional Units =====
        Length.LI to BigDecimal("0.000333333333"), // 厘 = 1/3000 m
        Length.FEN to BigDecimal("0.003333333333"), // 分 = 1/300 m
        Length.CUN to BigDecimal("0.033333333333"), // 寸 = 1/30 m
        Length.CHI to BigDecimal("0.333333333333"), // 尺 = 1/3 m
        Length.ZHANG to BigDecimal("3.333333333333"), // 丈 = 10 尺
        Length.CHINESE_MILE to BigDecimal("500") // 里 = 500 m
    )

    /**
     * Perform numerical conversions
     * (Double calculation, internally using BigDecimal to maintain accuracy).
     */
    @Override
    override fun convert(value: Double, from: Any, to: Any): Double {
        if (from !is Length || to !is Length) throw IllegalArgumentException("Invalid unit type")
        if (from == to) return value
        val valueInBigDecimal = value.toBigDecimal()
        val inMeters = valueInBigDecimal.multiply(toMeterFactor.getValue(from))
        val result = inMeters.divide(toMeterFactor.getValue(to), maxFractionDigits + 4, rounding)
        return result.toDouble()
    }
}
