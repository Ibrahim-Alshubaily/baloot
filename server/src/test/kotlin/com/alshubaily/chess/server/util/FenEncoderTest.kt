package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.util.FenEncoder.pieceToPlane
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class FenEncoderTest {

    private val startFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

    private fun isBitSet(bytes: ByteArray, bit: Int): Boolean {
        return (bytes[bit / 8].toInt() shr (bit % 8)) and 1 == 1
    }

    private fun bitIndex(plane: Int, rank: Int, file: Int): Int {
        return plane * 64 + rank * 8 + file
    }

    @Test
    fun `should encode starting position to 98 bytes`() {
        val encoded = FenEncoder.encodeBoardFeatures(startFEN)
        assertEquals(98, encoded.size)
        assertTrue(encoded.any { it.toInt() != 0 })
    }

    @Test
    fun `starting position - all major pieces should be in correct bit positions`() {
        val encoded = FenEncoder.encodeBoardFeatures(startFEN)

        // White major pieces
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['R']!!, 7, 0)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['N']!!, 7, 1)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['B']!!, 7, 2)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['Q']!!, 7, 3)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['K']!!, 7, 4)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['B']!!, 7, 5)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['N']!!, 7, 6)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['R']!!, 7, 7)))
        for (file in 0..7) {
            assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['P']!!, 6, file)))
        }

        // Black major pieces
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['r']!!, 0, 0)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['n']!!, 0, 1)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['b']!!, 0, 2)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['q']!!, 0, 3)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['k']!!, 0, 4)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['b']!!, 0, 5)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['n']!!, 0, 6)))
        assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['r']!!, 0, 7)))
        for (file in 0..7) {
            assertTrue(isBitSet(encoded, bitIndex(pieceToPlane['p']!!, 1, file)))
        }
    }

    @Test
    fun `should encode castling rights bits correctly`() {
        val encoded = FenEncoder.encodeBoardFeatures(startFEN)
        val bits = listOf(769, 770, 771, 772)
        bits.forEach { bit ->
            assertTrue(isBitSet(encoded, bit))
        }
    }

    @Test
    fun `should encode en passant file correctly`() {
        val encoded = FenEncoder.encodeBoardFeatures("8/8/8/8/8/8/8/8 w - e3 0 1")
        for (i in 0..7) {
            val bit = 773 + i
            if (i == 4) {
                assertTrue(isBitSet(encoded, bit), "Bit $bit for file 'e' should be set")
            } else {
                assertFalse(isBitSet(encoded, bit), "Bit $bit should be unset")
            }
        }
    }
}
