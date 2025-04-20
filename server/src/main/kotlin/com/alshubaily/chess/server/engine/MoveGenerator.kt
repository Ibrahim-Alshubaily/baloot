package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.makeMove

object MoveGenerator {

    fun generateLegalMoves(state: GameState): Set<Move> {
        // TODO: validate castling
        return generate(state).filter { move ->
            val next = makeMove(state, move)
            !isKingInCheck(next, state.currentPlayer)
        }.toSet()
    }

    fun generate(state: GameState): Set<Move> {
        return buildSet {
            addAll(PawnMoveGenerator.generate(state))
            addAll(HorseMoveGenerator.generate(state))
            addAll(SlidingPieceMoveGenerator.generateElephantMoves(state))
            addAll(SlidingPieceMoveGenerator.generateGiraffeMoves(state))
            addAll(SlidingPieceMoveGenerator.generateQueenMoves(state))
            addAll(KingMoveGenerator.generate(state))
        }
    }

    private fun isKingInCheck(
        state: GameState,
        defendingPlayer: Player
    ): Boolean {
        val king = if (defendingPlayer == Player.WHITE) state.board.whiteKing else state.board.blackKing
        val kingPosition = king.countTrailingZeroBits()
        return generate(state).any{ it -> it.to == kingPosition }
    }
}
