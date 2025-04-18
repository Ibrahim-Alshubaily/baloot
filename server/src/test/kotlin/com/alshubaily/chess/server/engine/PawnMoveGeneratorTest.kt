package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class PawnMoveGeneratorTest {

    @Test
    fun `initial position has 16 pawn moves per side`() {
        val whiteState = GameState(initialBitboards(), Player.WHITE)
        val blackState = GameState(initialBitboards(), Player.BLACK)

        assertEquals(16, PawnMoveGenerator.generate(whiteState).size)
        assertEquals(16, PawnMoveGenerator.generate(blackState).size)
    }

    @Test
    fun `white pawn forward moves`() {
        val from = index('A', 2)
        val state = GameState(
            board = Bitboards(whitePawns = 1L shl from),
            currentPlayer = Player.WHITE
        )
        val moves = PawnMoveGenerator.generate(state)

        assertEquals(
            setOf(
                Move(from, index('A', 3)),
                Move(from, index('A', 4))
            ),
            moves
        )
    }

    @Test
    fun `black pawn forward moves`() {
        val from = index('A', 7)
        val state = GameState(
            board = Bitboards(blackPawns = 1L shl from),
            currentPlayer = Player.BLACK
        )
        val moves = PawnMoveGenerator.generate(state)

        assertEquals(
            setOf(
                Move(from, index('A', 6)),
                Move(from, index('A', 5))
            ),
            moves
        )
    }

    @Test
    fun `blocked pawn does not move`() {
        val whitePawn = index('A', 2)
        val blocker = index('A', 3)
        val state = GameState(
            board = Bitboards(
                whitePawns = 1L shl whitePawn,
                blackPawns = 1L shl blocker
            ),
            currentPlayer = Player.WHITE
        )
        val moves = PawnMoveGenerator.generate(state)

        assertEquals(emptySet(), moves)
    }

    @Test
    fun `white pawn captures diagonally`() {
        val from = index('E', 4)
        val leftTarget = index('D', 5)
        val rightTarget = index('F', 5)
        val forwardBlock = index('E', 5)

        val state = GameState(
            board = Bitboards(
                whitePawns = 1L shl from,
                blackPawns = (1L shl leftTarget) or (1L shl rightTarget)  or (1L shl forwardBlock)
            ),
            currentPlayer = Player.WHITE
        )
        val moves = PawnMoveGenerator.generate(state)

        assertEquals(
            setOf(
                Move(from, leftTarget),
                Move(from, rightTarget)
            ),
            moves
        )
    }

    @Test
    fun `black pawn captures diagonally`() {
        val from = index('E', 5)
        val leftTarget = index('D', 4)
        val rightTarget = index('F', 4)
        val forwardBlock = index('E', 4)

        val state = GameState(
            board = Bitboards(
                blackPawns = 1L shl from,
                whitePawns = (1L shl leftTarget) or (1L shl rightTarget) or (1L shl forwardBlock)
            ),
            currentPlayer = Player.BLACK
        )
        val moves = PawnMoveGenerator.generate(state)

        assertEquals(
            setOf(
                Move(from, leftTarget),
                Move(from, rightTarget)
            ),
            moves
        )
    }
}
