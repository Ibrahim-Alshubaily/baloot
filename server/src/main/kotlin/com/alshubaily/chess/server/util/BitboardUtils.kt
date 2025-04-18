package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.model.Bitboards


val Bitboards.whitePieces: Long
    get() = whitePawns or whiteKnights or whiteBishops or whiteRooks or whiteQueens or whiteKing

val Bitboards.blackPieces: Long
    get() = blackPawns or blackKnights or blackBishops or blackRooks or blackQueens or blackKing

val Bitboards.occupied: Long
    get() = whitePieces or blackPieces

fun rank(square: Int): Int = square / 8
fun file(square: Int): Int = square % 8

fun onSameDiagonal(a: Int, b: Int): Boolean =
    kotlin.math.abs(rank(a) - rank(b)) == kotlin.math.abs(file(a) - file(b))

inline fun forEachSetBit(bits: Long, action: (Int) -> Unit) {
    var b = bits
    while (b != 0L) {
        val index = b.countTrailingZeroBits()
        action(index)
        b = b xor (1L shl index) // clear that bit
    }
}

fun shift(bb: Long, amount: Int): Long =
    when {
        amount > 0 -> bb shl amount
        amount < 0 -> bb ushr -amount
        else -> bb
    }

fun index(file: Char, rank: Int): Int {
    require(file in 'A'..'H') { "Invalid file: $file" }
    require(rank in 1..8) { "Invalid rank: $rank" }
    return (rank - 1) * 8 + (file.uppercaseChar() - 'A')
}