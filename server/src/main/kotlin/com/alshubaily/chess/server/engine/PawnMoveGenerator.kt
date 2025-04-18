package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.constants.*
import com.alshubaily.chess.server.util.*

object PawnMoveGenerator {

    fun generate(state: GameState): Set<Move> {
        val moves = mutableSetOf<Move>()

        val pawns = if (state.currentPlayer == Player.WHITE) state.board.whitePawns else state.board.blackPawns
        val empty = state.board.occupied.inv()
        val forward = if (state.currentPlayer == Player.WHITE) FORWARD else -FORWARD
        val startRank = if (state.currentPlayer == Player.WHITE) RANK_2 else RANK_7
        val opponentPieces = if (state.currentPlayer == Player.WHITE) state.board.blackPieces else state.board.whitePieces

        generateForwardPushes(pawns, empty, forward, startRank, moves)
        generateCaptures(pawns, opponentPieces, state.currentPlayer, state.enCroissantSquare, moves)
        return moves
    }

    private fun generateForwardPushes(
        pawns: Long,
        empty: Long,
        forward: Int,
        startRank: Long,
        moves: MutableSet<Move>
    ) {
        val singlePush = shift(pawns, forward) and empty
        forEachSetBit(singlePush) { to ->
            val from = to - forward
            moves.add(Move(from, to))
        }

        val doublePush = shift(shift(pawns and startRank, forward) and empty, forward) and empty
        forEachSetBit(doublePush) { to ->
            val from = to - 2 * forward
            moves.add(Move(from, to))
        }
    }

    private fun generateCaptures(
        pawns: Long,
        opponentPieces: Long,
        player: Player,
        enCroissantSquare: Int?,
        moves: MutableSet<Move>
    ) {
        val sign = if (player == Player.WHITE) 1 else -1

        val leftShift = sign * FORWARD_LEFT
        val rightShift = sign * FORWARD_RIGHT

        val leftMask = if (player == Player.WHITE) FILE_A else FILE_H
        val rightMask = if (player == Player.WHITE) FILE_H else FILE_A

        generateCapture(pawns, opponentPieces, leftShift, leftMask, moves)
        generateCapture(pawns, opponentPieces, rightShift, rightMask, moves)

        enCroissantSquare?.let { target ->
            val fromLeft = target - leftShift
            val fromRight = target - rightShift

            if ((pawns and leftMask.inv()) and (1L shl fromLeft) != 0L)
                moves.add(Move(fromLeft, target))

            if ((pawns and rightMask.inv()) and (1L shl fromRight) != 0L)
                moves.add(Move(fromRight, target))
        }
    }


    private fun generateCapture(
        pawns: Long,
        enemyPieces: Long,
        shiftAmount: Int,
        fileMask: Long,
        moves: MutableSet<Move>
    ) {
        val maskedPawns = pawns and fileMask.inv()
        val captures = shift(maskedPawns, shiftAmount) and enemyPieces
        forEachSetBit(captures) { to ->
            val from = to - shiftAmount
            moves.add(Move(from, to))
        }
    }
}
