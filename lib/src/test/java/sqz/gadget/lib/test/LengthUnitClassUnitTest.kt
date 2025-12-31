package sqz.gadget.lib.test

import sqz.gadget.lib.LengthUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class LengthUnitClassUnitTest {

    private fun testImperialUnits(lengthUnit: LengthUnit) {
        // inch -> foot
        val inchToFoot = lengthUnit.convert(12.0, LengthUnit.Length.INCH, LengthUnit.Length.FOOT)
        assertEquals(inchToFoot, 1.0, 0.0001)

        // foot -> yard
        val footToYard = lengthUnit.convert(3.0, LengthUnit.Length.FOOT, LengthUnit.Length.YARD)
        assertEquals(footToYard, 1.0, 0.0001)

        // yard -> mile
        val yardToMile = lengthUnit.convert(1760.0, LengthUnit.Length.YARD, LengthUnit.Length.MILE)
        assertEquals(yardToMile, 1.0, 0.0001)

        // mile -> meter
        val mileToMeter = lengthUnit.convert(0.5, LengthUnit.Length.MILE, LengthUnit.Length.M)
        assertEquals(mileToMeter, 804.672, 0.0001)

        // nautical mile -> kilometer
        val nmiToKm = lengthUnit.convert(1.0, LengthUnit.Length.NMI, LengthUnit.Length.KM)
        assertEquals(nmiToKm, 1.852, 0.0001)
    }

    private fun testSiUnits(lengthUnit: LengthUnit) {
        val mmToCm = lengthUnit.convert(10.0, LengthUnit.Length.MM, LengthUnit.Length.CM)
        assertEquals(mmToCm, 1.0, 0.0001)

        val cmToM = lengthUnit.convert(100.0, LengthUnit.Length.CM, LengthUnit.Length.M)
        assertEquals(cmToM, 1.0, 0.0001)

        val umToMm = lengthUnit.convert(1000.0, LengthUnit.Length.UM, LengthUnit.Length.MM)
        assertEquals(umToMm, 1.0, 0.0001)

        val nmToUm = lengthUnit.convert(1000.0, LengthUnit.Length.NM, LengthUnit.Length.UM)
        assertEquals(nmToUm, 1.0, 0.0001)
    }

    private fun testCrossSystem(lengthUnit: LengthUnit) {
        // meter -> foot
        val meterToFoot = lengthUnit.convert(1.0, LengthUnit.Length.M, LengthUnit.Length.FOOT)
        assertEquals(meterToFoot, 3.28084, 0.0001)

        // kilometer -> mile
        val kmToMile = lengthUnit.convert(1.0, LengthUnit.Length.KM, LengthUnit.Length.MILE)
        assertEquals(kmToMile, 0.621371, 0.0001)

        // inch -> millimeter
        val inchToMm = lengthUnit.convert(1.0, LengthUnit.Length.INCH, LengthUnit.Length.MM)
        assertEquals(inchToMm, 25.4, 0.0001)
    }

    private fun testChineseUnits(lengthUnit: LengthUnit) {
        val cunToCm = lengthUnit.convert(1.0, LengthUnit.Length.CUN, LengthUnit.Length.CM)
        assertEquals(cunToCm, 3.333333, 0.0001)

        val chiToM = lengthUnit.convert(3.0, LengthUnit.Length.CHI, LengthUnit.Length.M)
        assertEquals(chiToM, 1.0, 0.0001)

        val zhangToM = lengthUnit.convert(1.0, LengthUnit.Length.ZHANG, LengthUnit.Length.M)
        assertEquals(zhangToM, 3.333333, 0.0001)

        val chineseMileToKm =
            lengthUnit.convert(2.0, LengthUnit.Length.CHINESE_MILE, LengthUnit.Length.KM)
        assertEquals(chineseMileToKm, 1.0, 0.0001)
    }

    private fun testEdgeCases(lengthUnit: LengthUnit) {
        // from == to
        val sameUnit = lengthUnit.convert(123.456, LengthUnit.Length.M, LengthUnit.Length.M)
        assertEquals(sameUnit, 123.456, 0.0)

        // very small
        val pmToNm = lengthUnit.convert(1000.0, LengthUnit.Length.PM, LengthUnit.Length.NM)
        assertEquals(pmToNm, 1.0, 0.0001)

        // very large
        val kmToLy = lengthUnit.convert(1.0, LengthUnit.Length.LY, LengthUnit.Length.KM)
        assertEquals(kmToLy, 9.4607304725808E12, 1E6)
    }

    @Test
    fun testLengthUnit() {
        val lengthUnit = LengthUnit(9)

        assertEquals(lengthUnit.format(0.123456789), "0.123456789")
        assertEquals(lengthUnit.parse("3.1415926"), 3.1415926)
        assertEquals(lengthUnit.parse("8964坦克TiananmenSquareProtests&massacre"), null)

        this.testImperialUnits(lengthUnit)
        this.testSiUnits(lengthUnit)
        this.testCrossSystem(lengthUnit)
        this.testChineseUnits(lengthUnit)
        this.testEdgeCases(lengthUnit)
    }
}
