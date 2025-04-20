package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.model.Bitboards
import com.alshubaily.chess.server.model.GameState
import com.alshubaily.chess.server.model.Move
import com.alshubaily.chess.server.model.Player

fun makeMove(state: GameState, move: Move): GameState {
    // TODO: handle en croissant and castle moves
    val fromBit = 1L shl move.from
    val toBit = 1L shl move.to
    val b = state.board
    val isWhite = state.currentPlayer == Player.WHITE

    val allPieces = listOf(
        b.whitePawns, b.whiteKnights, b.whiteBishops,
        b.whiteRooks, b.whiteQueens, b.whiteKing,
        b.blackPawns, b.blackKnights, b.blackBishops,
        b.blackRooks, b.blackQueens, b.blackKing
    )

    val updatedPieces = applyMoveToBitboards(allPieces, isWhite, fromBit, toBit)

    return state.copy(
        board = Bitboards(
            updatedPieces[0], updatedPieces[1], updatedPieces[2],
            updatedPieces[3], updatedPieces[4], updatedPieces[5],
            updatedPieces[6], updatedPieces[7], updatedPieces[8],
            updatedPieces[9], updatedPieces[10], updatedPieces[11]
        ),
        currentPlayer = state.currentPlayer.opponent()
    )
}

fun applyMoveToBitboards(
    allPieces: List<Long>,
    isWhite: Boolean,
    fromBit: Long,
    toBit: Long
): List<Long> {
    if (isWhite) {
        return allPieces.subList(0, 6).map { it.move(fromBit, toBit) } +
                allPieces.subList(6, 12).map { it.capture(toBit) }
    }
    return allPieces.subList(0, 6).map { it.capture(toBit) } +
            allPieces.subList(6, 12).map { it.move(fromBit, toBit) }
}
