package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.model.Move

fun generateSlidingMoves(
    sources: Long,
    rays: Array<Long>,
    ownPieces: Long,
    occupied: Long
): Set<Move> {
    val moves = mutableSetOf<Move>()

    forEachSetBit(sources) { from ->
        var ray = rays[from]
        while (ray != 0L) {
            val to = ray.countTrailingZeroBits()
            val toBit = 1L shl to

            if ((toBit and ownPieces) != 0L) return@forEachSetBit

            moves.add(Move(from, to))

            if ((toBit and occupied) != 0L) return@forEachSetBit

            ray = ray xor toBit
        }
    }

    return moves
}
