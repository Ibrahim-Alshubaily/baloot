package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KingMoveGeneratorTests {

    @Test
    fun `king in center has 8 moves`() {
        val from = index('D', 4)
        val state = GameState(
            board = Bitboards(whiteKing = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = KingMoveGenerator.generate(state)

        assertEquals(
            setOf(
                Move(from, index('C', 3)), Move(from, index('D', 3)), Move(from, index('E', 3)),
                Move(from, index('C', 4)),                        Move(from, index('E', 4)),
                Move(from, index('C', 5)), Move(from, index('D', 5)), Move(from, index('E', 5)),
            ),
            moves
        )
    }

    @Test
    fun `king in corner has 3 moves`() {
        val from = index('A', 1)
        val state = GameState(
            board = Bitboards(whiteKing = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = KingMoveGenerator.generate(state)

        assertEquals(
            setOf(
                Move(from, index('A', 2)),
                Move(from, index('B', 1)),
                Move(from, index('B', 2))
            ),
            moves
        )
    }

    @Test
    fun `king on edge has 5 moves`() {
        val from = index('H', 4)
        val state = GameState(
            board = Bitboards(whiteKing = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = KingMoveGenerator.generate(state)

        assertEquals(
            setOf(
                Move(from, index('G', 3)), Move(from, index('H', 3)),
                Move(from, index('G', 4)),
                Move(from, index('G', 5)), Move(from, index('H', 5)),
            ),
            moves
        )
    }

    @Test
    fun `king cannot move to square occupied by own piece`() {
        val from = index('D', 4)
        val blocker = index('C', 3)
        val state = GameState(
            board = Bitboards(
                whiteKing = 1L shl from,
                whitePawns = 1L shl blocker
            ),
            currentPlayer = Player.WHITE
        )

        val moves = KingMoveGenerator.generate(state)

        assertFalse(Move(from, blocker) in moves)
        assertTrue(moves.size < 8)
    }

    @Test
    fun `king can capture opponent`() {
        val from = index('D', 4)
        val opponent = index('C', 3)
        val state = GameState(
            board = Bitboards(
                whiteKing = 1L shl from,
                blackPawns = 1L shl opponent
            ),
            currentPlayer = Player.WHITE
        )

        assertTrue(Move(from, opponent) in KingMoveGenerator.generate(state))
    }
}
