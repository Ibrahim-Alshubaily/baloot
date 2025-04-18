package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*

object PawnMoveGenerator {

    private const val RANK_2 = 0x000000000000FF00L
    private const val RANK_7 = 0x00FF000000000000L

    fun generate(state: GameState): List<Move> {
        val moves = mutableListOf<Move>()

        val pawns = if (state.currentPlayer == Player.WHITE) state.board.whitePawns else state.board.blackPawns
        val empty = state.board.occupied.inv()
        val forward = if (state.currentPlayer == Player.WHITE) 8 else -8
        val startRank = if (state.currentPlayer == Player.WHITE) RANK_2 else RANK_7

        generateForwardPushes(pawns, empty, forward, startRank, moves)

        return moves
    }

    private fun generateForwardPushes(
        pawns: Long,
        empty: Long,
        forward: Int,
        startRank: Long,
        moves: MutableList<Move>
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

    private fun shift(bb: Long, amount: Int): Long =
        when {
            amount > 0 -> bb shl amount
            amount < 0 -> bb ushr -amount
            else -> bb
        }
}
