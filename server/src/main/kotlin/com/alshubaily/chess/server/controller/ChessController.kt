package com.alshubaily.chess.server.controller

import com.alshubaily.chess.server.player.MachineLearningPlayer
import com.alshubaily.chess.server.service.ChessService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chess")
class ChessController(
    private val chessService: ChessService,
    private val machineLearningPlayer: MachineLearningPlayer
) {

    @PostMapping("/make-move")
    fun makeMove(@RequestBody moveRequest: MoveRequest): MoveResponse {
        val afterUserMove = chessService.makeMove(moveRequest).newFen
        val machineMove = machineLearningPlayer.selectBestMove(afterUserMove)
        return  chessService.makeMove(
            MoveRequest(
                fen = afterUserMove,
                move = machineMove
            )
        )
    }
}

data class MoveRequest(
    val fen: String,
    val move: String,
)

data class MoveResponse(
    val newFen: String,
)
