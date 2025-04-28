package com.alshubaily.chess.server.player

import com.alshubaily.chess.server.engine.ChessEngine
import com.alshubaily.chess.server.util.FenEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class MachineLearningPlayer(
    private val webClient: WebClient
) {
    fun selectBestMove(fen: String): String {
        val legalMoves = ChessEngine.getLegalMoves(fen)

        val featureVectors = legalMoves.map { move ->
            FenEncoder.encodeBoardFeaturesToFloats(ChessEngine.applyMove(fen, move))
        }

        val predictions = webClient.post()
            .uri("http://localhost:5005/predict")
            .bodyValue(mapOf("features" to featureVectors))
            .retrieve()
            .bodyToMono(List::class.java)
            .block() as List<Double>

        val isWhiteToMove = fen.contains(" w ")
        val bestMoveIndex = if (isWhiteToMove) {
            predictions.indices.maxByOrNull { predictions[it] }!!
        } else {
            predictions.indices.minByOrNull { predictions[it] }!!
        }
        return legalMoves[bestMoveIndex]
    }
}
