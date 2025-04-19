package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.model.Move

fun generateSlidingMoves(
    sources: Long,
    directions: IntArray,
    ownPieces: Long,
    occupied: Long
): Set<Move> {
    val moves = mutableSetOf<Move>()

    forEachSetBit(sources) { from ->
        for (dir in directions) {
            var to = from + dir
            while (to in 0..63 && onSameLine(from, to, dir)) {
                val toBit = 1L shl to
                if ((toBit and ownPieces) != 0L) break

                moves.add(Move(from, to))

                if ((toBit and occupied) != 0L) break
                to += dir
            }
        }
    }

    return moves
}

fun onSameLine(from: Int, to: Int, dir: Int): Boolean {
    val fromFile = from % 8
    val fromRank = from / 8
    val toFile = to % 8
    val toRank = to / 8

    return when (dir) {
        1, -1   -> fromRank == toRank
        8, -8   -> fromFile == toFile
        7, -7, 9, -9 -> kotlin.math.abs(fromFile - toFile) == kotlin.math.abs(fromRank - toRank)
        else    -> false
    }
}