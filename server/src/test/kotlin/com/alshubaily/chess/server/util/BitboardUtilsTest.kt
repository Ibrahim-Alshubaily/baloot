package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.model.initialBitboards
import kotlin.test.Test
import kotlin.test.assertEquals

class BitboardUtilsTest {

    private val board = initialBitboards()

    @Test
    fun `whitePieces bit count is 16`() {
        assertEquals(16, board.whitePieces.countOneBits())
    }

    @Test
    fun `blackPieces bit count is 16`() {
        assertEquals(16, board.blackPieces.countOneBits())
    }

    @Test
    fun `occupied bit count is 32`() {
        assertEquals(32, board.occupied.countOneBits())
    }
}
