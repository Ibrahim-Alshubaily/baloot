package com.alshubaily.chess.server.model

enum class Player {
    WHITE,
    BLACK;

    fun opponent(): Player = if (this == WHITE) BLACK else WHITE
}
