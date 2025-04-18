package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.GameState
import com.alshubaily.chess.server.model.Move
import com.alshubaily.chess.server.model.Player
import com.alshubaily.chess.server.util.blackPieces
import com.alshubaily.chess.server.util.forEachSetBit
import com.alshubaily.chess.server.util.whitePieces
import kotlin.math.abs

object HorseMoveGenerator {

    private val MOVES: LongArray = LongArray(64) { square ->
        val file = square % 8
        val rank = square / 8
        var movesMask = 0L

        for (fileOffset in listOf(-2, -1, 1, 2)) {
            for (rankOffset in listOf(-2, -1, 1, 2)) {
                if (abs(fileOffset) != abs(rankOffset)) {
                    val targetFile = file + fileOffset
                    val targetRank = rank + rankOffset

                    if (targetFile in 0..7 && targetRank in 0..7) {
                        val targetSquare = targetRank * 8 + targetFile
                        movesMask = movesMask or (1L shl targetSquare)
                    }
                }
            }
        }

        movesMask
    }

    fun generate(state: GameState): Set<Move> {
        val knights = if (state.currentPlayer == Player.WHITE) state.board.whiteKnights else state.board.blackKnights
        val ownPieces = if (state.currentPlayer == Player.WHITE) state.board.whitePieces else state.board.blackPieces

        val moves = mutableSetOf<Move>()

        forEachSetBit(knights) { from ->
            val destinations = MOVES[from] and ownPieces.inv()
            forEachSetBit(destinations) { to ->
                moves.add(Move(from, to))
            }
        }

        return moves
    }
}