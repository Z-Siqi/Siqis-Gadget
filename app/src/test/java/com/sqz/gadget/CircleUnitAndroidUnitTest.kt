package com.sqz.gadget

import org.junit.Assert.assertEquals
import org.junit.Test
import sqz.gadget.lib.CircleUnit
import sqz.gadget.lib.CircleUnit.Companion

class CircleUnitAndroidUnitTest {

    private fun testFromArea() {
        val circleCircle = CircleUnit(Companion.Circle.Area)
        circleCircle.setToUnit(Companion.Circle.Diameter)
        assertEquals(circleCircle.calculate(100.0), 11.28, 0.01)
        circleCircle.setToUnit(Companion.Circle.Circumference)
        assertEquals(circleCircle.calculate(100.0), 35.44, 0.01)
        circleCircle.setToUnit(Companion.Circle.Radius)
        assertEquals(circleCircle.calculate(100.0), 5.64, 0.01)
    }

    private fun testFromDiameter() {
        val circleCircle = CircleUnit(Companion.Circle.Diameter)
        circleCircle.setToUnit(Companion.Circle.Area)
        assertEquals(circleCircle.calculate(100.0), 7853.98, 0.01)
        circleCircle.setToUnit(Companion.Circle.Circumference)
        assertEquals(circleCircle.calculate(100.0), 314.15, 0.01)
        circleCircle.setToUnit(Companion.Circle.Radius)
        assertEquals(circleCircle.calculate(100.0), 50.0, 0.01)
    }

    private fun testFromCircumference() {
        val circleCircle = CircleUnit(Companion.Circle.Circumference)
        circleCircle.setToUnit(Companion.Circle.Area)
        assertEquals(circleCircle.calculate(100.0), 795.77, 0.01)
        circleCircle.setToUnit(Companion.Circle.Diameter)
        assertEquals(circleCircle.calculate(100.0), 31.83, 0.01)
        circleCircle.setToUnit(Companion.Circle.Radius)
        assertEquals(circleCircle.calculate(100.0), 15.91, 0.01)
    }

    private fun testFromRadius() {
        val circleCircle = CircleUnit(Companion.Circle.Radius)
        circleCircle.setToUnit(Companion.Circle.Area)
        assertEquals(circleCircle.calculate(100.0), 31415.92, 0.01)
        circleCircle.setToUnit(Companion.Circle.Diameter)
        assertEquals(circleCircle.calculate(100.0), 200.0, 0.01)
        circleCircle.setToUnit(Companion.Circle.Circumference)
        assertEquals(circleCircle.calculate(100.0), 628.31, 0.01)
    }

    @Test
    fun testCircleUnit() {
        this.testFromArea()
        this.testFromDiameter()
        this.testFromCircumference()
        this.testFromRadius()
    }
}
