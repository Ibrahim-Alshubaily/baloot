package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QueenMoveGeneratorTests {

    @Test
    fun `queen in center generates full rank, file, and diagonal moves`() {
        val from = index('D', 4)
        val state = GameState(
            board = Bitboards(whiteQueens = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = SlidingPieceMoveGenerator.generateQueenMoves(state)

        val expected = mutableSetOf<Move>()

        for (f in 0..7) {
            val to = rank(from) * 8 + f
            if (to != from) expected.add(Move(from, to))
        }

        for (r in 0..7) {
            val to = r * 8 + file(from)
            if (to != from) expected.add(Move(from, to))
        }

        for (to in 0 until 64) {
            if (to != from && onSameDiagonal(from, to)) {
                expected.add(Move(from, to))
            }
        }

        assertEquals(expected, moves)
    }

    @Test
    fun `queen is blocked by own piece`() {
        val from = index('D', 4)
        val blocker = index('F', 6)
        val state = GameState(
            board = Bitboards(
                whiteQueens = 1L shl from,
                whitePawns = 1L shl blocker
            ),
            currentPlayer = Player.WHITE
        )

        val moves = SlidingPieceMoveGenerator.generateQueenMoves(state)

        assertFalse(Move(from, blocker) in moves)
        assertFalse(Move(from, index('G', 7)) in moves)
    }

    @Test
    fun `queen can capture enemy but not beyond`() {
        val from = index('D', 4)
        val target = index('B', 6)
        val state = GameState(
            board = Bitboards(
                whiteQueens = 1L shl from,
                blackPawns = 1L shl target
            ),
            currentPlayer = Player.WHITE
        )

        val moves = SlidingPieceMoveGenerator.generateQueenMoves(state)

        assertTrue(Move(from, target) in moves)
        assertFalse(Move(from, index('A', 7)) in moves)
    }

    @Test
    fun `queen in corner limited to one quadrant`() {
        val from = index('A', 1)
        val state = GameState(
            board = Bitboards(whiteQueens = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = SlidingPieceMoveGenerator.generateQueenMoves(state)

        val expected = mutableSetOf<Move>()

        for (r in 1..7)
            expected.add(Move(from, index('A', r + 1)))

        for (f in 1..7)
            expected.add(Move(from, index(('A' + f).toChar(), 1)))

        for (i in 1..7)
            expected.add(Move(from, from + i * 9))

        assertEquals(expected, moves)
    }
}
