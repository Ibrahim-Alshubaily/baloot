package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*

object MoveGenerator {

    fun generate(state: GameState): Set<Move> {
        val moves = mutableSetOf<Move>()

        moves += PawnMoveGenerator.generate(state)
        moves += HorseMoveGenerator.generate(state)
        moves += SlidingPieceMoveGenerator.generateElephantMoves(state)
        moves += SlidingPieceMoveGenerator.generateGiraffeMoves(state)
        moves += SlidingPieceMoveGenerator.generateQueenMoves(state)
        moves += KingMoveGenerator.generate(state)

        return moves
    }
}
