package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.model.Bitboards


val Bitboards.whitePieces: Long
    get() = whitePawns or whiteKnights or whiteBishops or whiteRooks or whiteQueens or whiteKing

val Bitboards.blackPieces: Long
    get() = blackPawns or blackKnights or blackBishops or blackRooks or blackQueens or blackKing

val Bitboards.occupied: Long
    get() = whitePieces or blackPieces
