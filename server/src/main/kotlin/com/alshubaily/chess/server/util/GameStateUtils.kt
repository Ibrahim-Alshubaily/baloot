package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.constants.FORWARD
import com.alshubaily.chess.server.model.Bitboards
import com.alshubaily.chess.server.model.GameState
import com.alshubaily.chess.server.model.Move
import com.alshubaily.chess.server.model.Player
import kotlin.math.abs

fun makeMove(state: GameState, move: Move): GameState {
    // TODO: handle castle moves
    val fromBit = 1L shl move.from
    val toBit = 1L shl move.to
    val b = state.board
    val isWhite = state.currentPlayer == Player.WHITE
    val isPawn = (state.board.whitePawns and fromBit != 0L) or (state.board.blackPawns and fromBit != 0L)

    val allPieces = listOf(
        b.whitePawns, b.whiteKnights, b.whiteBishops,
        b.whiteRooks, b.whiteQueens, b.whiteKing,
        b.blackPawns, b.blackKnights, b.blackBishops,
        b.blackRooks, b.blackQueens, b.blackKing
    )

    val capturedPosition = getCapturedPosition(state, move)
    val capturedBit = 1L shl capturedPosition
    val updatedPieces = applyMove(allPieces, isWhite, fromBit, toBit, capturedBit)

    val enCroissantSquare = move.takeIf {
        isPawn && abs(it.from / 8 - it.to / 8) == 2
    }?.let { (it.from + it.to) / 2 }

    return state.copy(
        board = Bitboards(
            updatedPieces[0], updatedPieces[1], updatedPieces[2],
            updatedPieces[3], updatedPieces[4], updatedPieces[5],
            updatedPieces[6], updatedPieces[7], updatedPieces[8],
            updatedPieces[9], updatedPieces[10], updatedPieces[11]
        ),
        currentPlayer = state.currentPlayer.opponent(),
        enCroissantSquare = enCroissantSquare
    )
}


fun applyMove(
    allPieces: List<Long>,
    isWhite: Boolean,
    fromBit: Long,
    toBit: Long,
    capturedBit: Long
): List<Long> {
    if (isWhite) {
        return allPieces.subList(0, 6).map { it.move(fromBit, toBit) } +
                allPieces.subList(6, 12).map { it.capture(capturedBit) }
    }
    return allPieces.subList(0, 6).map { it.capture(capturedBit) } +
            allPieces.subList(6, 12).map { it.move(fromBit, toBit) }
}

fun getCapturedPosition(state : GameState, move: Move): Int {
    if (move.to != state.enCroissantSquare) {
        return move.to
    }

    val fromBit = 1L shl move.from
    val isPawn = (state.board.whitePawns and fromBit != 0L) or (state.board.blackPawns and fromBit != 0L)
    if (!isPawn || (move.from % 8 == move.to % 8)) {
        return move.to
    }

    return if (state.currentPlayer == Player.WHITE) {
        move.to - FORWARD
    } else {
        move.to + FORWARD
    }

}
