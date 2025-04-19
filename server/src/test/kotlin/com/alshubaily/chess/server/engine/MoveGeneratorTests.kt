package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.index
import kotlin.test.Test
import kotlin.test.assertEquals

class MoveGeneratorTest {

    @Test
    fun `initial position has 20 pseudo-legal moves for each side`() {
        val whiteState = GameState(
            board = initialBitboards(),
            currentPlayer = Player.WHITE,
            castlingRights = setOf(
                CastlingRight.WHITE_KINGSIDE,
                CastlingRight.WHITE_QUEENSIDE
            )
        )
        val blackState = whiteState.copy(currentPlayer = Player.BLACK,
            castlingRights = setOf(
                CastlingRight.BLACK_KINGSIDE,
                CastlingRight.BLACK_QUEENSIDE
            )
        )

        assertEquals(20, MoveGenerator.generate(whiteState).size)
        assertEquals(20, MoveGenerator.generate(blackState).size)
    }

    @Test
    fun `single king only has 8 or fewer moves`() {
        val state = GameState(
            board = Bitboards(whiteKing = 1L shl index('D', 4)),
            currentPlayer = Player.WHITE
        )

        val moves = MoveGenerator.generate(state)
        assertEquals(8, moves.size)
    }
}
