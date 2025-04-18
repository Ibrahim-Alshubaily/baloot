package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*

object ElephantMoveGenerator {

    val RAYS: Array<Long> = Array(64) { from ->
        var mask = 0L
        for (to in 0 until 64) {
            if (to != from && onSameDiagonal(from, to)) {
                mask = mask or (1L shl to)
            }
        }
        mask
    }

    fun generate(state: GameState): Set<Move> {
        val bishops = if (state.currentPlayer == Player.WHITE) state.board.whiteBishops else state.board.blackBishops
        val ownPieces = if (state.currentPlayer == Player.WHITE) state.board.whitePieces else state.board.blackPieces
        val occupied = state.board.occupied

        val moves = mutableSetOf<Move>()

        forEachSetBit(bishops) { from ->
            var visible = RAYS[from]
            while (visible != 0L) {
                val to = visible.countTrailingZeroBits()
                val toBit = 1L shl to

                if ((toBit and ownPieces) != 0L) break

                moves.add(Move(from, to))

                if ((toBit and occupied) != 0L) break

                visible = visible xor toBit
            }
        }


        return moves
    }
}
