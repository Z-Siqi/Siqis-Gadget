package sqz.gadget.lib.calculate

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * A unit converter for common hormone lab results.
 *
 * Supported hormones:
 * - Estradiol (E2)        : Steroid hormone (mass <-> molar conversion supported)
 * - Testosterone (T)      : Steroid hormone (mass <-> molar conversion supported)
 * - Progesterone (P4)     : Steroid hormone (mass <-> molar conversion supported)
 * - Luteinizing (LH)      : Glycoprotein hormone (IU-only conversion supported)
 * - FollicleStimulating (FSH): Glycoprotein hormone (IU-only conversion supported)
 * - Prolactin (PRL)       : Protein hormone (IU <-> mass supported, depends on WHO standard)
 *
 * -------------------------------------------------------------------------
 * Key rules (important):
 * 1) You MUST NOT convert between different hormones.
 *    Example: Estradiol -> Testosterone is not allowed and will throw.
 *
 * 2) Steroid hormones (E2/T/P4) allow:
 *    - MASS units   (e.g., pg/mL, ng/dL, ug/L, ...)
 *    - MOLAR units  (e.g., pmol/L, nmol/mL, ...)
 *    Conversion between MASS <-> MOLAR is done using molecular weight (exact).
 *
 * 3) LH / FSH allow ONLY IU-family units:
 *    - IU/L, mIU/mL, IU/mL, mIU/L, ...
 *    You cannot convert LH/FSH into mass or molar units.
 *
 * 4) PRL allows IU-family and mass-family conversion:
 *    - IU-family: mIU/L, uIU/mL, IU/L, ...
 *    - mass-family: ng/mL, ug/L, ...
 *    BUT: IU <-> mass conversion depends on lab calibration (WHO standard).
 *    Default standard used here: WHO_84_500 (commonly 1 ng/mL = 21.2 mIU/L).
 *
 * -------------------------------------------------------------------------
 * Recommended usage pattern (UI-friendly and safest):
 * - Always pass MeasureUnit created via: `Hormone.X.with(ConcentrationUnit.Y)`
 *
 * Example:
 * ```
 *  // Create a converter (default PRL standard: WHO_84_500, rounding: HALF_UP)
 *  val converter = HormoneUnit(maxFractionDigits = 4)
 *
 *  // 1) Parse text input from UI (EditText / TextField)
 *  val inputValue: Double? = converter.parse("50.0")
 *  if (inputValue == null) {
 *      // show error to user
 *  } else {
 *      // 2) Example: Estradiol (E2) pg/mL -> pmol/L
 *      val e2PmolPerL = converter.convert(
 *          value = inputValue,
 *          from = HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.PG_PER_ML),
 *          to   = HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.PMOL_PER_L)
 *      )
 *      // 3) Format for display
 *      val display = converter.format(e2PmolPerL)
 *      println("E2 = $display ${HormoneUnit.ConcentrationUnit.PMOL_PER_L}")
 *  }
 * ```
 *
 * -------------------------------------------------------------------------
 * Input / output:
 * - `convert()` takes Double and returns Double (internally uses BigDecimal for accuracy).
 * - `format()` gives a user-friendly decimal string (no scientific notation).
 * - `parse()` can parse user input text into Double? safely.
 */
@Suppress("SpellCheckingInspection")
class HormoneUnit : UnitConverter {

    private val prolactinStandard: ProlactinStandard
    private val mathContext: MathContext

    /**
     * Constructs a HormoneUnit converter.
     *
     * @param maxFractionDigits The maximum number of fraction digits for the output.
     * @param prolactinStandard The standard used for Prolactin unit conversions (IU <-> Mass).
     * @param rounding The rounding mode to use for calculations.
     */
    constructor(
        maxFractionDigits: Int = 8,
        prolactinStandard: ProlactinStandard = ProlactinStandard.WHO_84_500,
        rounding: RoundingMode = RoundingMode.HALF_UP
    ) : super(maxFractionDigits = maxFractionDigits, rounding = rounding) {
        this.prolactinStandard = prolactinStandard
        this.mathContext = MathContext(34, rounding) // high precision
    }

    enum class Hormone {
        Estradiol,
        Testosterone,
        Progesterone,
        Luteinizing,
        FollicleStimulating,
        Prolactin;

        /**
         * Creates a [MeasureUnit] by combining this [Hormone] with a [ConcentrationUnit].
         */
        fun with(unit: ConcentrationUnit): MeasureUnit = MeasureUnit(this, unit)
    }

    enum class UnitFamily { MASS, MOLAR, IU }

    /**
     * A single unit token (UI-friendly). toString() returns stable symbol like "pg/mL".
     *
     * multiplierToPerLiter meaning:
     * - MASS: grams per liter for 1 unit
     * - MOLAR: moles per liter for 1 unit
     * - IU: IU per liter for 1 unit
     */
    enum class ConcentrationUnit(
        val family: UnitFamily,
        private val multiplierToPerLiter: BigDecimal,
        private val symbol: String
    ) {
        // ========================
        // MASS concentration units
        // Canonical: g/L
        // ========================

        // femto
        FG_PER_L(UnitFamily.MASS, pow10(-15), "fg/L"),
        FG_PER_ML(UnitFamily.MASS, pow10(-12), "fg/mL"),
        FG_PER_DL(UnitFamily.MASS, pow10(-14), "fg/dL"),
        FG_PER_UL(UnitFamily.MASS, pow10(-9), "fg/uL"),

        // pico
        PG_PER_L(UnitFamily.MASS, pow10(-12), "pg/L"),
        PG_PER_ML(UnitFamily.MASS, pow10(-9), "pg/mL"),
        PG_PER_DL(UnitFamily.MASS, pow10(-11), "pg/dL"),
        PG_PER_UL(UnitFamily.MASS, pow10(-6), "pg/uL"),
        PG_PER_100_ML(UnitFamily.MASS, pow10(-11), "pg/100mL"), // alias pg/dL
        PG_PERCENT(UnitFamily.MASS, pow10(-11), "pg%"),         // old alias of pg/dL

        // nano
        NG_PER_L(UnitFamily.MASS, pow10(-9), "ng/L"),
        NG_PER_ML(UnitFamily.MASS, pow10(-6), "ng/mL"),
        NG_PER_DL(UnitFamily.MASS, pow10(-8), "ng/dL"),
        NG_PER_UL(UnitFamily.MASS, pow10(-3), "ng/uL"),
        NG_PER_100_ML(UnitFamily.MASS, pow10(-8), "ng/100mL"),  // alias ng/dL
        NG_PERCENT(UnitFamily.MASS, pow10(-8), "ng%"),          // old alias of ng/dL

        // micro
        UG_PER_L(UnitFamily.MASS, pow10(-6), "ug/L"),
        UG_PER_ML(UnitFamily.MASS, pow10(-3), "ug/mL"),
        UG_PER_DL(UnitFamily.MASS, pow10(-5), "ug/dL"),
        UG_PER_UL(UnitFamily.MASS, pow10(0), "ug/uL"),
        MICROGRAM_PER_LITER(UnitFamily.MASS, pow10(-6), "µg/L"),
        MICROGRAM_PER_ML(UnitFamily.MASS, pow10(-3), "µg/mL"),
        MICROGRAM_PER_DL(UnitFamily.MASS, pow10(-5), "µg/dL"),
        MICROGRAM_PER_UL(UnitFamily.MASS, pow10(0), "µg/µL"),

        // milli
        MG_PER_L(UnitFamily.MASS, pow10(-3), "mg/L"),
        MG_PER_ML(UnitFamily.MASS, pow10(0), "mg/mL"),
        MG_PER_DL(UnitFamily.MASS, pow10(-2), "mg/dL"),

        // base
        G_PER_L(UnitFamily.MASS, pow10(0), "g/L"),
        G_PER_ML(UnitFamily.MASS, pow10(3), "g/mL"),
        G_PER_DL(UnitFamily.MASS, pow10(1), "g/dL"),

        // ==========================
        // MOLAR concentration units
        // Canonical: mol/L
        // ==========================

        // femto
        FMOL_PER_L(UnitFamily.MOLAR, pow10(-15), "fmol/L"),
        FMOL_PER_ML(UnitFamily.MOLAR, pow10(-12), "fmol/mL"),
        FMOL_PER_DL(UnitFamily.MOLAR, pow10(-14), "fmol/dL"),
        FMOL_PER_UL(UnitFamily.MOLAR, pow10(-9), "fmol/uL"),

        // pico
        PMOL_PER_L(UnitFamily.MOLAR, pow10(-12), "pmol/L"),
        PMOL_PER_ML(UnitFamily.MOLAR, pow10(-9), "pmol/mL"),  // user asked example
        PMOL_PER_DL(UnitFamily.MOLAR, pow10(-11), "pmol/dL"),
        PMOL_PER_UL(UnitFamily.MOLAR, pow10(-6), "pmol/uL"),

        // nano
        NMOL_PER_L(UnitFamily.MOLAR, pow10(-9), "nmol/L"),
        NMOL_PER_ML(UnitFamily.MOLAR, pow10(-6), "nmol/mL"),
        NMOL_PER_DL(UnitFamily.MOLAR, pow10(-8), "nmol/dL"),
        NMOL_PER_UL(UnitFamily.MOLAR, pow10(-3), "nmol/uL"),

        // micro
        UMOL_PER_L(UnitFamily.MOLAR, pow10(-6), "umol/L"),
        UMOL_PER_ML(UnitFamily.MOLAR, pow10(-3), "umol/mL"),
        UMOL_PER_DL(UnitFamily.MOLAR, pow10(-5), "umol/dL"),
        UMOL_PER_UL(UnitFamily.MOLAR, pow10(0), "umol/uL"),

        // milli
        MMOL_PER_L(UnitFamily.MOLAR, pow10(-3), "mmol/L"),
        MMOL_PER_ML(UnitFamily.MOLAR, pow10(0), "mmol/mL"),
        MMOL_PER_DL(UnitFamily.MOLAR, pow10(-2), "mmol/dL"),

        // base
        MOL_PER_L(UnitFamily.MOLAR, pow10(0), "mol/L"),
        MOL_PER_ML(UnitFamily.MOLAR, pow10(3), "mol/mL"),
        MOL_PER_DL(UnitFamily.MOLAR, pow10(1), "mol/dL"),

        // ======================
        // IU concentration units
        // Canonical: IU/L
        // ======================
        IU_PER_L(UnitFamily.IU, pow10(0), "IU/L"),
        IU_PER_ML(UnitFamily.IU, pow10(3), "IU/mL"),
        IU_PER_DL(UnitFamily.IU, pow10(1), "IU/dL"),

        MIU_PER_L(UnitFamily.IU, pow10(-3), "mIU/L"),
        MIU_PER_ML(UnitFamily.IU, pow10(0), "mIU/mL"),     // equals IU/L numerically
        MIU_PER_DL(UnitFamily.IU, pow10(-2), "mIU/dL"),

        UIU_PER_L(UnitFamily.IU, pow10(-6), "uIU/L"),
        UIU_PER_ML(UnitFamily.IU, pow10(-3), "uIU/mL"),     // equals mIU/L numerically
        UIU_PER_DL(UnitFamily.IU, pow10(-5), "uIU/dL"),

        // "U" treated as IU within the same assay context
        U_PER_L(UnitFamily.IU, pow10(0), "U/L"),
        U_PER_ML(UnitFamily.IU, pow10(3), "U/mL"),
        U_PER_DL(UnitFamily.IU, pow10(1), "U/dL");

        override fun toString(): String = symbol

        /**
         * Normalizes a [value] to the canonical "per liter" value of its family.
         */
        fun toPerLiter(value: BigDecimal, mc: MathContext): BigDecimal {
            return value.multiply(multiplierToPerLiter, mc)
        }

        /**
         * Converts a canonical "per liter" value back to this unit's scale.
         */
        fun fromPerLiter(perLiter: BigDecimal, mc: MathContext): BigDecimal {
            return perLiter.divide(multiplierToPerLiter, mc)
        }

        companion object {
            fun List<ConcentrationUnit>.asSortedList(): List<ConcentrationUnit> {
                val sort = this
                    .groupBy { it.toString().first() }
                    .values
                    .flatten()
                return sort
            }
        }
    }

    data class MeasureUnit(val hormone: Hormone, val unit: ConcentrationUnit) {
        override fun toString(): String = unit.toString()
    }

    /**
     * PRL IU<->mass depends on lab calibration standard.
     * Default: WHO 3rd IS 84/500, commonly 1 ng/mL = 21.2 mIU/L.
     */
    @Suppress("unused")
    enum class ProlactinStandard(
        val mIUPerNgPerMl: BigDecimal,
        val displayName: String
    ) {
        WHO_84_500(BigDecimal("21.2"), "WHO 3rd IS 84/500")
    }

    // Molecular weights (g/mol) for steroid hormones
    private val molecularWeightGPerMol: Map<Hormone, BigDecimal> = mapOf(
        Hormone.Estradiol to BigDecimal("272.38"),
        Hormone.Testosterone to BigDecimal("288.42"),
        Hormone.Progesterone to BigDecimal("314.46")
    )

    /**
     * Returns a list of [ConcentrationUnit]s that are applicable for the given [hormone].
     *
     * @param hormone The hormone to check for applicable units.
     * @return A list of valid concentration units for the hormone.
     */
    @Suppress("unused")
    fun applicableUnits(hormone: Hormone): List<ConcentrationUnit> {
        val families = allowedFamilies(hormone)
        return ConcentrationUnit.entries.filter { it.family in families }
    }

    /**
     * Checks if a [unit] is applicable for a specific [hormone].
     *
     * @param hormone The hormone to check.
     * @param unit The unit to verify.
     * @return `true` if the unit is applicable, `false` otherwise.
     */
    fun isApplicable(hormone: Hormone, unit: ConcentrationUnit): Boolean =
        unit.family in allowedFamilies(hormone)

    private fun allowedFamilies(hormone: Hormone): Set<UnitFamily> = when (hormone) {
        Hormone.Estradiol, Hormone.Testosterone, Hormone.Progesterone ->
            setOf(UnitFamily.MASS, UnitFamily.MOLAR)

        Hormone.Luteinizing, Hormone.FollicleStimulating ->
            setOf(UnitFamily.IU)

        Hormone.Prolactin ->
            setOf(UnitFamily.IU, UnitFamily.MASS)
    }

    /**
     * Converts a hormone concentration value from one unit to another.
     *
     * @param value The numerical value to convert.
     * @param from The source unit specification. Can be [MeasureUnit] or [Pair] of [Hormone] and [ConcentrationUnit].
     * @param to The target unit specification. Can be [MeasureUnit] or [Pair] of [Hormone] and [ConcentrationUnit].
     * @return The converted value as a Double.
     * @throws IllegalArgumentException if hormones do not match or units are incompatible.
     */
    override fun convert(value: Double, from: Any, to: Any): Double {
        val fromSpec = normalizeSpec(from)
        val toSpec = normalizeSpec(to)

        if (fromSpec.hormone != toSpec.hormone) {
            throw IllegalArgumentException(
                "Cannot convert between different hormones: ${fromSpec.hormone} -> ${toSpec.hormone}"
            )
        }

        val hormone = fromSpec.hormone
        val fromUnit = fromSpec.unit
        val toUnit = toSpec.unit

        if (!isApplicable(hormone, fromUnit) || !isApplicable(hormone, toUnit)) {
            throw IllegalArgumentException("Unit not applicable for hormone=$hormone: from=$fromUnit to=$toUnit")
        }

        val input = BigDecimal.valueOf(value)

        return when (hormone) {
            Hormone.Estradiol, Hormone.Testosterone, Hormone.Progesterone -> {
                val molarMass = molecularWeightGPerMol[hormone]
                    ?: throw IllegalStateException("Missing molecular weight for $hormone")

                // Canonical: mol/L
                val canonicalMolPerLiter: BigDecimal = when (fromUnit.family) {
                    UnitFamily.MOLAR -> fromUnit.toPerLiter(input, mathContext)
                    UnitFamily.MASS -> {
                        val gramsPerLiter = fromUnit.toPerLiter(input, mathContext)
                        gramsPerLiter.divide(molarMass, mathContext) // (g/L)/(g/mol)=mol/L
                    }

                    UnitFamily.IU -> throw IllegalArgumentException("IU units not valid for steroid hormone $hormone")
                }

                val output: BigDecimal = when (toUnit.family) {
                    UnitFamily.MOLAR -> toUnit.fromPerLiter(canonicalMolPerLiter, mathContext)
                    UnitFamily.MASS -> {
                        val gramsPerLiter = canonicalMolPerLiter.multiply(molarMass, mathContext)
                        toUnit.fromPerLiter(gramsPerLiter, mathContext)
                    }

                    UnitFamily.IU -> throw IllegalArgumentException("IU units not valid for steroid hormone $hormone")
                }

                output.toDouble()
            }

            Hormone.Luteinizing, Hormone.FollicleStimulating -> {
                if (fromUnit.family != UnitFamily.IU || toUnit.family != UnitFamily.IU) {
                    throw IllegalArgumentException("LH/FSH only support IU-based units: from=$fromUnit to=$toUnit")
                }
                val canonicalIUPerLiter = fromUnit.toPerLiter(input, mathContext)
                val output = toUnit.fromPerLiter(canonicalIUPerLiter, mathContext)
                output.toDouble()
            }

            Hormone.Prolactin -> {
                // Canonical: IU/L
                val canonicalIUPerLiter: BigDecimal = when (fromUnit.family) {
                    UnitFamily.IU -> fromUnit.toPerLiter(input, mathContext)
                    UnitFamily.MASS -> {
                        val gramsPerLiter = fromUnit.toPerLiter(input, mathContext)

                        // g/L -> ng/mL: 1 g/L = 1e6 ng/mL
                        val ngPerMl = gramsPerLiter.multiply(BigDecimal("1000000"), mathContext)

                        // ng/mL -> mIU/L -> IU/L
                        val mIUPerLiter =
                            ngPerMl.multiply(prolactinStandard.mIUPerNgPerMl, mathContext)
                        mIUPerLiter.divide(BigDecimal("1000"), mathContext)
                    }

                    UnitFamily.MOLAR -> throw IllegalArgumentException("Molar units not supported for Prolactin")
                }

                val output: BigDecimal = when (toUnit.family) {
                    UnitFamily.IU -> toUnit.fromPerLiter(canonicalIUPerLiter, mathContext)
                    UnitFamily.MASS -> {
                        // IU/L -> mIU/L -> ng/mL -> g/L -> target mass unit
                        val mIUPerLiter =
                            canonicalIUPerLiter.multiply(BigDecimal("1000"), mathContext)
                        val ngPerMl =
                            mIUPerLiter.divide(prolactinStandard.mIUPerNgPerMl, mathContext)
                        val gramsPerLiter = ngPerMl.divide(BigDecimal("1000000"), mathContext)
                        toUnit.fromPerLiter(gramsPerLiter, mathContext)
                    }

                    UnitFamily.MOLAR -> throw IllegalArgumentException("Molar units not supported for Prolactin")
                }

                output.toDouble()
            }
        }
    }

    /**
     * Normalize Any -> MeasureUnit.
     * Supported:
     * - MeasureUnit
     * - Pair<Hormone, ConcentrationUnit>
     */
    private fun normalizeSpec(any: Any): MeasureUnit {
        return when (any) {
            is MeasureUnit -> any
            is Pair<*, *> -> {
                val hormone = any.first as? Hormone
                    ?: throw IllegalArgumentException("Pair.first must be Hormone")
                val unit = any.second as? ConcentrationUnit
                    ?: throw IllegalArgumentException("Pair.second must be ConcentrationUnit")
                MeasureUnit(hormone, unit)
            }

            else -> throw IllegalArgumentException(
                "Invalid unit type: ${any::class.qualifiedName}. Use MeasureUnit or Pair<Hormone, ConcentrationUnit>."
            )
        }
    }

    companion object {
        private fun pow10(power: Int): BigDecimal = BigDecimal.ONE.scaleByPowerOfTen(power)
    }
}
