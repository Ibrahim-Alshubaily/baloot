package com.alshubaily.chess.server.model

enum class CastlingRight {
    WHITE_KINGSIDE,
    WHITE_QUEENSIDE,
    BLACK_KINGSIDE,
    BLACK_QUEENSIDE
}

fun initialCastlingRights(): Set<CastlingRight> = setOf(
    CastlingRight.WHITE_KINGSIDE,
    CastlingRight.WHITE_QUEENSIDE,
    CastlingRight.BLACK_KINGSIDE,
    CastlingRight.BLACK_QUEENSIDE
)