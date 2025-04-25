package com.alshubaily.chess.server.util

import com.fasterxml.jackson.databind.JsonNode

object EvalEncoder {

    fun encodeFromJson(node: JsonNode): Pair<ByteArray, ByteArray>? {
        val fen = node["fen"]?.asText() ?: return null
        val evals = node["evals"] ?: return null

        val bestEval = evals.maxByOrNull { it["depth"]?.asInt() ?: -1 } ?: return null
        val pvs = bestEval["pvs"] ?: return null
        if (!pvs.elements().hasNext()) return null

        val bestPv = pvs[0]
        val score = when {
            bestPv.has("cp") -> normalizeCp(bestPv["cp"].asInt())
            bestPv.has("mate") -> {
                val mate = bestPv["mate"].asInt()
                if (mate > 0) 1.0f else -1.0f
            }
            else -> return null
        }

        val boardBytes = FenEncoder.encodeBoardFeatures(fen)
        val scoreBytes = encodeScore(score)
        return boardBytes to scoreBytes
    }

    private fun normalizeCp(cp: Int): Float {
        return cp.coerceIn(-50, 50) / 100f
    }

    private fun encodeScore(score: Float): ByteArray {
        val clamped = score.coerceIn(-1.0f, 1.0f)
        val int16 = (clamped * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
        return byteArrayOf((int16 shr 8).toByte(), (int16 and 0xFF).toByte())
    }
}
