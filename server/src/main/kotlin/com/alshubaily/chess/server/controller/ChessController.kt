package com.alshubaily.chess.server.controller

import com.alshubaily.chess.server.model.Move
import com.alshubaily.chess.server.service.ChessService
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

@RestController
@RequestMapping("/chess")
class ChessController(private val chessService: ChessService) {

    @PostMapping("/make-move")
    fun makeMove(@RequestBody moveRequest: MoveRequest): MoveResponse {
        return chessService.makeMove(moveRequest)
    }
}

data class MoveRequest(
    val fen: String,
    val move: String,
)

data class MoveResponse(
    val newFen: String,
)
