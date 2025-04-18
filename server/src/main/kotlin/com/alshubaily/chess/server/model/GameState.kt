package com.alshubaily.chess.server.model

data class GameState(
    val board: Bitboards,
    val currentPlayer: Player = Player.WHITE,
    val castlingRights: Set<CastlingRight> = emptySet(),
    val enCroissantSquare: Int? = null
)