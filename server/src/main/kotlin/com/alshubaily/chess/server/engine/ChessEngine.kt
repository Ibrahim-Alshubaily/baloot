package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.Move
import com.alshubaily.chess.server.util.FenParser
import com.alshubaily.chess.server.util.FenEncoder
import com.alshubaily.chess.server.util.makeMove


object ChessEngine {

    fun getLegalMoves(fen: String): List<String> {
        val gameState = FenParser.parse(fen)
        return MoveGenerator.generateLegalMoves(gameState).map { it.toUci() }
    }

    fun applyMove(fen: String, moveUci: String): String {
        val gameState = FenParser.parse(fen)
        val move = Move.fromUci(moveUci)
        return FenEncoder.encode(makeMove(gameState, move))
    }
}
