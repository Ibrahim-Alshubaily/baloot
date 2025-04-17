package com.alshubaily.chess.server.model

import kotlin.test.Test
import kotlin.test.assertEquals

class BitboardsTest {

    private val board = initialBitboards()

    @Test
    fun `white piece counts are correct`() {
        assertEquals(8, board.whitePawns.countOneBits())
        assertEquals(2, board.whiteKnights.countOneBits())
        assertEquals(2, board.whiteBishops.countOneBits())
        assertEquals(2, board.whiteRooks.countOneBits())
        assertEquals(1, board.whiteQueens.countOneBits())
        assertEquals(1, board.whiteKing.countOneBits())
    }

    @Test
    fun `black piece counts are correct`() {
        assertEquals(8, board.blackPawns.countOneBits())
        assertEquals(2, board.blackKnights.countOneBits())
        assertEquals(2, board.blackBishops.countOneBits())
        assertEquals(2, board.blackRooks.countOneBits())
        assertEquals(1, board.blackQueens.countOneBits())
        assertEquals(1, board.blackKing.countOneBits())
    }
}
