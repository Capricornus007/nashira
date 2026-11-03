package io.github.capricornus007.nashira.desktop

import java.awt.Rectangle
import kotlin.test.Test
import kotlin.test.assertEquals

class XimCandidateCoordinateContractTest {
    @Test
    fun `candidate spot is caret lower edge relative to window`() {
        val spot = XimCandidateCoordinateContract.spot(
            caretScreenBounds = Rectangle(460, 330, 2, 24),
            clientScreenBounds = Rectangle(120, 80, 900, 640),
            windowScreenBounds = Rectangle(100, 60, 940, 680),
            scaleX = 1.0,
            scaleY = 1.0,
        )

        assertEquals(360, spot.x)
        assertEquals(294, spot.y)
    }

    @Test
    fun `scrolled caret is clamped to visible client`() {
        val spot = XimCandidateCoordinateContract.spot(
            caretScreenBounds = Rectangle(410, 900, 2, 24),
            clientScreenBounds = Rectangle(140, 100, 800, 500),
            windowScreenBounds = Rectangle(100, 60, 880, 560),
            scaleX = 1.0,
            scaleY = 1.0,
        )

        assertEquals(310, spot.x)
        assertEquals(539, spot.y)
    }

    @Test
    fun `hidpi scales window relative coordinates`() {
        val spot = XimCandidateCoordinateContract.spot(
            caretScreenBounds = Rectangle(350, 220, 2, 20),
            clientScreenBounds = Rectangle(110, 70, 600, 400),
            windowScreenBounds = Rectangle(100, 50, 640, 440),
            scaleX = 2.0,
            scaleY = 2.0,
        )

        assertEquals(500, spot.x)
        assertEquals(380, spot.y)
    }

    @Test
    fun `moving window and caret together preserves XIM spot`() {
        val before = XimCandidateCoordinateContract.spot(
            caretScreenBounds = Rectangle(260, 180, 2, 20),
            clientScreenBounds = Rectangle(110, 70, 600, 400),
            windowScreenBounds = Rectangle(100, 50, 640, 440),
            scaleX = 1.5,
            scaleY = 1.5,
        )
        val after = XimCandidateCoordinateContract.spot(
            caretScreenBounds = Rectangle(660, 380, 2, 20),
            clientScreenBounds = Rectangle(510, 270, 600, 400),
            windowScreenBounds = Rectangle(500, 250, 640, 440),
            scaleX = 1.5,
            scaleY = 1.5,
        )

        assertEquals(before, after)
    }
}
