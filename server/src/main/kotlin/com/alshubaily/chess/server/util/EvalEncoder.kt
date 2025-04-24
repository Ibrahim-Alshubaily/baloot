package com.alshubaily.chess.server.util

import com.fasterxml.jackson.databind.JsonNode

object EvalEncoder {

    /**
     * Extracts the best score and encodes board + score as Kafka-ready payloads.
     * Returns null if no valid evaluation exists.
     */
    fun encodeFromJson(node: JsonNode): Pair<ByteArray, ByteArray>? {
        val fen = node["fen"]?.asText() ?: return null
        val evals = node["evals"] ?: return null

        val bestEval = evals.maxByOrNull { it["depth"]?.asInt() ?: -1 } ?: return null
        val pvs = bestEval["pvs"] ?: return null
        if (!pvs.elements().hasNext()) return null

        val bestPv = pvs[0]
        val score = when {
            bestPv.has("cp") -> bestPv["cp"].asInt().coerceIn(-1000, 1000)
            bestPv.has("mate") -> if (bestPv["mate"].asInt() > 0) 1000 else -1000
            else -> return null
        }

        val boardBytes = FenEncoder.encodeBoardFeatures(fen)
        val scoreBytes = encodeScore(score)
        return boardBytes to scoreBytes
    }

    private fun encodeScore(score: Int): ByteArray {
        val clamped = score.coerceIn(-1000, 1000)
        return byteArrayOf((clamped shr 8).toByte(), (clamped and 0xFF).toByte())
    }
}
