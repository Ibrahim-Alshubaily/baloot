package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*

object SlidingPieceMoveGenerator {

    private val DIAGONAL_DIRECTIONS = intArrayOf(7, 9, -7, -9)
    private val STRAIGHT_DIRECTIONS = intArrayOf(8, -8, 1, -1)
    private val ALL_DIRECTIONS = DIAGONAL_DIRECTIONS + STRAIGHT_DIRECTIONS

    fun generateElephantMoves(state: GameState): Set<Move> {
        val bishops = if (state.currentPlayer == Player.WHITE) state.board.whiteBishops else state.board.blackBishops
        val own = if (state.currentPlayer == Player.WHITE) state.board.whitePieces else state.board.blackPieces
        return generateSlidingMoves(bishops, DIAGONAL_DIRECTIONS, own, state.board.occupied)
    }

    fun generateGiraffeMoves(state: GameState): Set<Move> {
        val rooks = if (state.currentPlayer == Player.WHITE) state.board.whiteRooks else state.board.blackRooks
        val own = if (state.currentPlayer == Player.WHITE) state.board.whitePieces else state.board.blackPieces
        return generateSlidingMoves(rooks, STRAIGHT_DIRECTIONS, own, state.board.occupied)
    }

    fun generateQueenMoves(state: GameState): Set<Move> {
        val queens = if (state.currentPlayer == Player.WHITE) state.board.whiteQueens else state.board.blackQueens
        val own = if (state.currentPlayer == Player.WHITE) state.board.whitePieces else state.board.blackPieces
        return generateSlidingMoves(queens, ALL_DIRECTIONS, own, state.board.occupied)
    }
}
