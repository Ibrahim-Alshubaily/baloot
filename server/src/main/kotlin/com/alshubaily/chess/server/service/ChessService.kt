package com.alshubaily.chess.server.service

import com.alshubaily.chess.server.controller.MoveRequest
import com.alshubaily.chess.server.controller.MoveResponse
import com.alshubaily.chess.server.engine.ChessEngine
import org.springframework.stereotype.Service

@Service
class ChessService {

    fun getLegalMoves(fen: String): List<String> {
        return ChessEngine.getLegalMoves(fen)
    }

    fun makeMove(moveRequest: MoveRequest): MoveResponse {
        val legalMoves = ChessEngine.getLegalMoves(moveRequest.fen)
        if (legalMoves.contains(moveRequest.move)) {
            return MoveResponse(ChessEngine.applyMove(moveRequest.fen, moveRequest.move))
        }
        return MoveResponse(moveRequest.fen)
    }
}
