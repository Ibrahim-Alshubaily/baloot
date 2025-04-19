package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KingMoveGeneratorTests {

    @Test
    fun `initial position has 0 King moves`() {
        val whiteState = GameState(initialBitboards(), Player.WHITE)
        val blackState = GameState(initialBitboards(), Player.BLACK)

        assertEquals(0, KingMoveGenerator.generate(whiteState).size)
        assertEquals(0, KingMoveGenerator.generate(blackState).size)
    }

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
                Move(from, index('C', 5)), Move(from, index('D', 5)), Move(from, index('E', 5))
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
        assertEquals(
            setOf(
                Move(from, index('A', 2)),
                Move(from, index('B', 1)),
                Move(from, index('B', 2))
            ),
            KingMoveGenerator.generate(state)
        )
    }

    @Test
    fun `king on edge has 5 moves`() {
        val from = index('H', 4)
        val state = GameState(
            board = Bitboards(whiteKing = 1L shl from),
            currentPlayer = Player.WHITE
        )
        assertEquals(
            setOf(
                Move(from, index('G', 3)), Move(from, index('H', 3)),
                Move(from, index('G', 4)),
                Move(from, index('G', 5)), Move(from, index('H', 5))
            ),
            KingMoveGenerator.generate(state)
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
    }

    @Test
    fun `king can capture opponent`() {
        val from = index('D', 4)
        val enemy = index('C', 3)
        val state = GameState(
            board = Bitboards(
                whiteKing = 1L shl from,
                blackPawns = 1L shl enemy
            ),
            currentPlayer = Player.WHITE
        )
        assertTrue(Move(from, enemy) in KingMoveGenerator.generate(state))
    }

    @Test
    fun `white castling both sides`() {
        val state = GameState(
            board = Bitboards(
                whiteKing = 1L shl index('E', 1),
                whiteRooks = (1L shl index('A', 1)) or (1L shl index('H', 1))
            ),
            currentPlayer = Player.WHITE,
            castlingRights = setOf(
                CastlingRight.WHITE_KINGSIDE,
                CastlingRight.WHITE_QUEENSIDE
            )
        )
        val moves = KingMoveGenerator.generate(state)
        assertTrue(Move(index('E', 1), index('G', 1)) in moves)
        assertTrue(Move(index('E', 1), index('C', 1)) in moves)
    }

    @Test
    fun `black castling kingside only`() {
        val state = GameState(
            board = Bitboards(
                blackKing = 1L shl index('E', 8),
                blackRooks = 1L shl index('H', 8)
            ),
            currentPlayer = Player.BLACK,
            castlingRights = setOf(CastlingRight.BLACK_KINGSIDE)
        )
        val moves = KingMoveGenerator.generate(state)
        assertTrue(Move(index('E', 8), index('G', 8)) in moves)
        assertFalse(Move(index('E', 8), index('C', 8)) in moves)
    }

    @Test
    fun `black queenside castling blocked`() {
        val state = GameState(
            board = Bitboards(
                blackKing = 1L shl index('E', 8),
                blackRooks = 1L shl index('A', 8),
                whitePawns = 1L shl index('C', 8)
            ),
            currentPlayer = Player.BLACK,
            castlingRights = setOf(CastlingRight.BLACK_QUEENSIDE)
        )
        assertFalse(Move(index('E', 8), index('C', 8)) in KingMoveGenerator.generate(state))
    }
}
