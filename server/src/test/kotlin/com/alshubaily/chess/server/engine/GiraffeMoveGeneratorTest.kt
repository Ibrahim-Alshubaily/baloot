package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GiraffeMoveGeneratorTest {

    @Test
    fun `giraffe in center moves along rank and file`() {
        val from = index('D', 4)
        val state = GameState(
            board = Bitboards(whiteRooks = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = SlidingPieceMoveGenerator.generateGiraffeMoves(state)

        val expected = mutableSetOf<Move>()

        for (file in 0..7) {
            val to = rank(from) * 8 + file
            if (to != from) expected.add(Move(from, to))
        }

        for (rank in 0..7) {
            val to = rank * 8 + file(from)
            if (to != from) expected.add(Move(from, to))
        }

        assertEquals(expected, moves)
    }

    @Test
    fun `giraffe is blocked by own piece`() {
        val from = index('D', 4)
        val blocker = index('D', 6)
        val state = GameState(
            board = Bitboards(
                whiteRooks = 1L shl from,
                whitePawns = 1L shl blocker
            ),
            currentPlayer = Player.WHITE
        )

        val moves = SlidingPieceMoveGenerator.generateGiraffeMoves(state)

        assertFalse(Move(from, blocker) in moves)
        assertFalse(Move(from, index('D', 7)) in moves)
    }

    @Test
    fun `giraffe can capture enemy but not beyond`() {
        val from = index('D', 4)
        val target = index('D', 6)
        val state = GameState(
            board = Bitboards(
                whiteRooks = 1L shl from,
                blackPawns = 1L shl target
            ),
            currentPlayer = Player.WHITE
        )

        val moves = SlidingPieceMoveGenerator.generateGiraffeMoves(state)

        assertTrue(Move(from, target) in moves)
        assertFalse(Move(from, index('D', 7)) in moves)
    }

    @Test
    fun `giraffe in corner has limited range`() {
        val from = index('A', 1)
        val state = GameState(
            board = Bitboards(whiteRooks = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = SlidingPieceMoveGenerator.generateGiraffeMoves(state)

        assertEquals(
            setOf(
                Move(from, index('A', 2)),
                Move(from, index('A', 3)),
                Move(from, index('A', 4)),
                Move(from, index('A', 5)),
                Move(from, index('A', 6)),
                Move(from, index('A', 7)),
                Move(from, index('A', 8)),

                Move(from, index('B', 1)),
                Move(from, index('C', 1)),
                Move(from, index('D', 1)),
                Move(from, index('E', 1)),
                Move(from, index('F', 1)),
                Move(from, index('G', 1)),
                Move(from, index('H', 1))
            ),
            moves
        )
    }
}
