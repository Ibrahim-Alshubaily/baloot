package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.model.Bitboards


val Bitboards.whitePieces: Long
    get() = whitePawns or whiteKnights or whiteBishops or whiteRooks or whiteQueens or whiteKing

val Bitboards.blackPieces: Long
    get() = blackPawns or blackKnights or blackBishops or blackRooks or blackQueens or blackKing

val Bitboards.occupied: Long
    get() = whitePieces or blackPieces

fun Long.move(from: Long, to: Long): Long =
    if ((this and from) != 0L) (this xor from) or to else this

fun Long.capture(to: Long): Long =
    this and to.inv()

fun rank(square: Int): Int = square / 8
fun file(square: Int): Int = square % 8

fun onSameDiagonal(a: Int, b: Int): Boolean =
    kotlin.math.abs(rank(a) - rank(b)) == kotlin.math.abs(file(a) - file(b))

inline fun forEachSetBit(bits: Long, action: (Int) -> Unit) {
    var b = bits
    while (b != 0L) {
        val index = b.countTrailingZeroBits()
        action(index)
        b = b xor (1L shl index)
    }
}

fun shift(bb: Long, amount: Int): Long =
    when {
        amount > 0 -> bb shl amount
        amount < 0 -> bb ushr -amount
        else -> bb
    }

fun index(file: Char, rank: Int): Int {
    return (rank - 1) * 8 + (file.uppercaseChar() - 'A')
}

fun pieceAt(board: Bitboards, square: Int): Char? {
    val bit = 1L shl square
    return when {
        board.whitePawns and bit != 0L -> 'P'
        board.whiteKnights and bit != 0L -> 'N'
        board.whiteBishops and bit != 0L -> 'B'
        board.whiteRooks and bit != 0L -> 'R'
        board.whiteQueens and bit != 0L -> 'Q'
        board.whiteKing and bit != 0L -> 'K'
        board.blackPawns and bit != 0L -> 'p'
        board.blackKnights and bit != 0L -> 'n'
        board.blackBishops and bit != 0L -> 'b'
        board.blackRooks and bit != 0L -> 'r'
        board.blackQueens and bit != 0L -> 'q'
        board.blackKing and bit != 0L -> 'k'
        else -> null
    }
}