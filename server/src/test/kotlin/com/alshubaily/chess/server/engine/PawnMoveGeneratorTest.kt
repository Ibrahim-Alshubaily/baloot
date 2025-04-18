package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
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
        val state = GameState(
            board = Bitboards(whitePawns = 1L shl 8), // A2
            currentPlayer = Player.WHITE
        )
        val moves = PawnMoveGenerator.generate(state)

        assertEquals(
            setOf(Move(8, 16), Move(8, 24)), // A2→A3, A2→A4
            moves.toSet()
        )
    }

    @Test
    fun `black pawn forward moves`() {
        val state = GameState(
            board = Bitboards(blackPawns = 1L shl 48), // A7
            currentPlayer = Player.BLACK
        )
        val moves = PawnMoveGenerator.generate(state)

        assertEquals(
            setOf(Move(48, 40), Move(48, 32)), // A7→A6, A7→A5
            moves.toSet()
        )
    }

    @Test
    fun `blocked pawn does not move`() {
        val state = GameState(
            board = Bitboards(
                whitePawns = 1L shl 8,     // A2
                blackPawns = 1L shl 16     // A3 blocks A2
            ),
            currentPlayer = Player.WHITE
        )
        val moves = PawnMoveGenerator.generate(state)

        assertEquals(emptyList(), moves)
    }
}
