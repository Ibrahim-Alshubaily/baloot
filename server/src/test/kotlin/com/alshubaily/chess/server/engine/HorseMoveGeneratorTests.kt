package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HorseMoveGeneratorTests {

    @Test
    fun `horse in center has 8 moves`() {
        val from = index('D', 4)
        val state = GameState(
            board = Bitboards(whiteKnights = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = HorseMoveGenerator.generate(state)

        assertEquals(8, moves.size)
        assertEquals(
            setOf(
                Move(from, index('C', 2)),
                Move(from, index('E', 2)),
                Move(from, index('B', 3)),
                Move(from, index('F', 3)),
                Move(from, index('B', 5)),
                Move(from, index('F', 5)),
                Move(from, index('C', 6)),
                Move(from, index('E', 6)),
            ),
            moves
        )
    }

    @Test
    fun `horse does not jump the fence`() {
        val from = index('A', 1)
        val state = GameState(
            board = Bitboards(whiteKnights = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = HorseMoveGenerator.generate(state)

        assertEquals(
            setOf(
                Move(from, index('B', 3)),
                Move(from, index('C', 2))
            ),
            moves
        )
    }

    @Test
    fun `horse does not stomp teammates`() {
        val from = index('D', 4)
        val blocked1 = index('C', 2)
        val blocked2 = index('E', 2)

        val state = GameState(
            board = Bitboards(
                whiteKnights = 1L shl from,
                whitePawns = (1L shl blocked1) or (1L shl blocked2)
            ),
            currentPlayer = Player.WHITE
        )

        val moves = HorseMoveGenerator.generate(state)

        assertEquals(6, moves.size)
        assertEquals(
            setOf(
                Move(from, index('B', 3)),
                Move(from, index('F', 3)),
                Move(from, index('B', 5)),
                Move(from, index('F', 5)),
                Move(from, index('C', 6)),
                Move(from, index('E', 6)),
            ),
            moves
        )
    }
}
