package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*

object KingMoveGenerator {

    private val MOVES: Array<Long> = Array(64) { square ->
        val file = file(square)
        val rank = rank(square)
        var mask = 0L

        for (df in -1..1) {
            for (dr in -1..1) {
                if (df == 0 && dr == 0) continue
                val f = file + df
                val r = rank + dr
                if (f in 0..7 && r in 0..7) {
                    mask = mask or (1L shl (r * 8 + f))
                }
            }
        }

        mask
    }

    fun generate(state: GameState): Set<Move> {
        val king = if (state.currentPlayer == Player.WHITE) state.board.whiteKing else state.board.blackKing
        val ownPieces = if (state.currentPlayer == Player.WHITE) state.board.whitePieces else state.board.blackPieces

        val moves = mutableSetOf<Move>()

        val from = king.countTrailingZeroBits()
        val targets = MOVES[from] and ownPieces.inv()

        forEachSetBit(targets) { to ->
            moves.add(Move(from, to))
        }

        generateCastling(state, from, moves)

        return moves
    }

    private fun generateCastling(state: GameState, from: Int, moves: MutableSet<Move>) {
        val occupied = state.board.occupied

        fun tryCastle(right: CastlingRight, clearMask: Long, to: Int) {
            if (right in state.castlingRights && (occupied and clearMask) == 0L) {
                moves.add(Move(from, to))
            }
        }

        if (state.currentPlayer == Player.WHITE) {
            tryCastle(CastlingRight.WHITE_KINGSIDE, 0x60L, index('G', 1))
            tryCastle(CastlingRight.WHITE_QUEENSIDE, 0x0EL, index('C', 1))
        } else {
            tryCastle(CastlingRight.BLACK_KINGSIDE, 0x6000000000000000L, index('G', 8))
            tryCastle(CastlingRight.BLACK_QUEENSIDE, 0x0E00000000000000L, index('C', 8))
        }
    }

}
