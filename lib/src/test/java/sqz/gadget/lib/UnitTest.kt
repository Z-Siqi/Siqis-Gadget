package sqz.gadget.lib

import sqz.gadget.lib.test.CircleUnitClassUnitTest
import sqz.gadget.lib.test.LengthUnitClassUnitTest
import kotlin.test.Test

class UnitTest {
    @Test
    fun testCircleUnit() {
        CircleUnitClassUnitTest().testCircleUnit()
        LengthUnitClassUnitTest().testLengthUnit()
    }
}
