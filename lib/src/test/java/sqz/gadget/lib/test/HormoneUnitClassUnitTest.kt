package sqz.gadget.lib.test

import org.junit.jupiter.api.Assertions.assertThrows
import sqz.gadget.lib.HormoneUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("SpellCheckingInspection")
class HormoneUnitClassUnitTest {
    private val converter = HormoneUnit(maxFractionDigits = 12)

    private fun assertClose(actual: Double, expected: Double, eps: Double = 1e-9) {
        assertTrue(
            kotlin.math.abs(actual - expected) <= eps,
            "Expected $expected but got $actual (eps=$eps)"
        )
    }

    @Test
    fun `E2 pg_per_mL equals ng_per_L`() {
        val v = converter.convert(
            12.345,
            HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.PG_PER_ML),
            HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.NG_PER_L)
        )
        assertClose(v, 12.345)
    }

    @Test
    fun `E2 pg_per_mL to pmol_per_L`() {
        // 1 pg/mL = 1e-9 g/L
        // mol/L = (1e-9 / 272.38) = 3.671...e-12 mol/L = 3.671... pmol/L
        val v = converter.convert(
            1.0,
            HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.PG_PER_ML),
            HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.PMOL_PER_L)
        )
        assertClose(v, 3.671, eps = 1e-3) // allow small rounding tolerance
    }

    @Test
    fun `E2 pmol_per_mL to pmol_per_L is x1000`() {
        val v = converter.convert(
            2.5,
            HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.PMOL_PER_ML),
            HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.PMOL_PER_L)
        )
        assertClose(v, 2500.0)
    }

    @Test
    fun `Testosterone ng_per_dL to nmol_per_L`() {
        // 1 ng/dL = 1e-8 g/L
        // For 100 ng/dL => 1e-6 g/L
        // mol/L = 1e-6 / 288.42 = 3.467e-9 mol/L = 3.467 nmol/L
        val v = converter.convert(
            100.0,
            HormoneUnit.Hormone.Testosterone.with(HormoneUnit.ConcentrationUnit.NG_PER_DL),
            HormoneUnit.Hormone.Testosterone.with(HormoneUnit.ConcentrationUnit.NMOL_PER_L)
        )
        assertClose(v, 3.467, eps = 1e-3)
    }

    @Test
    fun `Progesterone ng_per_mL to nmol_per_L`() {
        // 1 ng/mL = 1e-6 g/L
        // mol/L = 1e-6 / 314.46 = 3.18e-9 mol/L = 3.18 nmol/L
        val v = converter.convert(
            1.0,
            HormoneUnit.Hormone.Progesterone.with(HormoneUnit.ConcentrationUnit.NG_PER_ML),
            HormoneUnit.Hormone.Progesterone.with(HormoneUnit.ConcentrationUnit.NMOL_PER_L)
        )
        assertClose(v, 3.18, eps = 1e-2)
    }

    @Test
    fun `LH mIU_per_mL equals IU_per_L numerically`() {
        val v = converter.convert(
            12.3,
            HormoneUnit.Hormone.Luteinizing.with(HormoneUnit.ConcentrationUnit.MIU_PER_ML),
            HormoneUnit.Hormone.Luteinizing.with(HormoneUnit.ConcentrationUnit.IU_PER_L)
        )
        assertClose(v, 12.3)
    }

    @Test
    fun `LH IU_per_mL to IU_per_L`() {
        val v = converter.convert(
            1.0,
            HormoneUnit.Hormone.Luteinizing.with(HormoneUnit.ConcentrationUnit.IU_PER_ML),
            HormoneUnit.Hormone.Luteinizing.with(HormoneUnit.ConcentrationUnit.IU_PER_L)
        )
        assertClose(v, 1000.0)
    }

    @Test
    fun `PRL ng_per_mL to mIU_per_L using WHO_84_500`() {
        // 1 ng/mL = 21.2 mIU/L
        val v = converter.convert(
            10.0,
            HormoneUnit.Hormone.Prolactin.with(HormoneUnit.ConcentrationUnit.NG_PER_ML),
            HormoneUnit.Hormone.Prolactin.with(HormoneUnit.ConcentrationUnit.MIU_PER_L)
        )
        assertClose(v, 212.0, eps = 1e-9)
    }

    @Test
    fun `PRL roundtrip mIU_per_L to ng_per_mL`() {
        val ng = converter.convert(
            212.0,
            HormoneUnit.Hormone.Prolactin.with(HormoneUnit.ConcentrationUnit.MIU_PER_L),
            HormoneUnit.Hormone.Prolactin.with(HormoneUnit.ConcentrationUnit.NG_PER_ML)
        )
        assertClose(ng, 10.0, eps = 1e-9)
    }

    @Test
    fun `Cannot convert between different hormones`() {
        assertThrows(IllegalArgumentException::class.java) {
            converter.convert(
                1.0,
                HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.PG_PER_ML),
                HormoneUnit.Hormone.Testosterone.with(HormoneUnit.ConcentrationUnit.PG_PER_ML)
            )
        }
    }

    @Test
    fun `LH cannot use mass units`() {
        assertThrows(IllegalArgumentException::class.java) {
            converter.convert(
                1.0,
                HormoneUnit.Hormone.Luteinizing.with(HormoneUnit.ConcentrationUnit.IU_PER_L),
                HormoneUnit.Hormone.Luteinizing.with(HormoneUnit.ConcentrationUnit.NG_PER_ML)
            )
        }
    }

    @Test
    fun `Steroid cannot use IU units`() {
        assertThrows(IllegalArgumentException::class.java) {
            converter.convert(
                1.0,
                HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.PG_PER_ML),
                HormoneUnit.Hormone.Estradiol.with(HormoneUnit.ConcentrationUnit.IU_PER_L)
            )
        }
    }

    @Test
    fun `Unit symbol toString is stable`() {
        assertEquals("pmol/mL", HormoneUnit.ConcentrationUnit.PMOL_PER_ML.toString())
        assertEquals("pg/uL", HormoneUnit.ConcentrationUnit.PG_PER_UL.toString())
        assertEquals("IU/dL", HormoneUnit.ConcentrationUnit.IU_PER_DL.toString())
    }

    fun testHormoneUnit() {
        this.`E2 pg_per_mL equals ng_per_L`()
        this.`E2 pg_per_mL to pmol_per_L`()
        this.`E2 pmol_per_mL to pmol_per_L is x1000`()
        this.`Testosterone ng_per_dL to nmol_per_L`()
        this.`Progesterone ng_per_mL to nmol_per_L`()
        this.`LH mIU_per_mL equals IU_per_L numerically`()
        this.`LH IU_per_mL to IU_per_L`()
        this.`PRL ng_per_mL to mIU_per_L using WHO_84_500`()
        this.`PRL roundtrip mIU_per_L to ng_per_mL`()
        this.`Cannot convert between different hormones`()
        this.`LH cannot use mass units`()
        this.`Steroid cannot use IU units`()
        this.`Unit symbol toString is stable`()
    }
}