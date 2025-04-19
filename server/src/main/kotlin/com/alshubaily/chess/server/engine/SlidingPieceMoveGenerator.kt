package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*

object SlidingPieceMoveGenerator {

    val DIAGONAL_RAYS: Array<Long> = Array(64) { from ->
        var mask = 0L
        for (to in 0 until 64) {
            if (to != from && onSameDiagonal(from, to)) {
                mask = mask or (1L shl to)
            }
        }
        mask
    }

    val STRAIGHT_RAYS: Array<Long> = Array(64) { from ->
        var mask = 0L
        val rf = rank(from)
        val ff = file(from)
        for (to in 0 until 64) {
            if (to != from && (rank(to) == rf || file(to) == ff)) {
                mask = mask or (1L shl to)
            }
        }
        mask
    }

    val QUEEN_RAYS : Array<Long> = Array(64) { from ->
        STRAIGHT_RAYS[from] or DIAGONAL_RAYS[from]
    }

    fun generateElephantMoves(state: GameState): Set<Move> {
        val bishops = if (state.currentPlayer == Player.WHITE) state.board.whiteBishops else state.board.blackBishops
        val own = if (state.currentPlayer == Player.WHITE) state.board.whitePieces else state.board.blackPieces
        return generateSlidingMoves(bishops, DIAGONAL_RAYS, own, state.board.occupied)
    }

    fun generateGiraffeMoves(state: GameState): Set<Move> {
        val rooks = if (state.currentPlayer == Player.WHITE) state.board.whiteRooks else state.board.blackRooks
        val own = if (state.currentPlayer == Player.WHITE) state.board.whitePieces else state.board.blackPieces
        return generateSlidingMoves(rooks, STRAIGHT_RAYS, own, state.board.occupied)
    }

    fun generateQueenMoves(state: GameState): Set<Move> {
        val queens = if (state.currentPlayer == Player.WHITE) state.board.whiteQueens else state.board.blackQueens
        val own = if (state.currentPlayer == Player.WHITE) state.board.whitePieces else state.board.blackPieces
        return generateSlidingMoves(queens, QUEEN_RAYS, own, state.board.occupied)
    }
}
