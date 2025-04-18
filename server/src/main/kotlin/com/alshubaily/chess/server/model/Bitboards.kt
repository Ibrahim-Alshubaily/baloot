package com.alshubaily.chess.server.model

data class Bitboards(
    val whitePawns: Long = 0L,
    val whiteKnights: Long = 0L,
    val whiteBishops: Long = 0L,
    val whiteRooks: Long = 0L,
    val whiteQueens: Long = 0L,
    val whiteKing: Long = 0L,

    val blackPawns: Long = 0L,
    val blackKnights: Long = 0L,
    val blackBishops: Long = 0L,
    val blackRooks: Long = 0L,
    val blackQueens: Long = 0L,
    val blackKing: Long = 0L
)

fun initialBitboards(): Bitboards = Bitboards(
    whitePawns   = (8..15).fold(0L) { acc, i -> acc or (1L shl i) },
    whiteRooks   = (1L shl 0) or (1L shl 7),
    whiteKnights = (1L shl 1) or (1L shl 6),
    whiteBishops = (1L shl 2) or (1L shl 5),
    whiteQueens  = (1L shl 3),
    whiteKing    = (1L shl 4),

    blackPawns   = (48..55).fold(0L) { acc, i -> acc or (1L shl i) },
    blackRooks   = (1L shl 56) or (1L shl 63),
    blackKnights = (1L shl 57) or (1L shl 62),
    blackBishops = (1L shl 58) or (1L shl 61),
    blackQueens  = (1L shl 59),
    blackKing    = (1L shl 60)
)
