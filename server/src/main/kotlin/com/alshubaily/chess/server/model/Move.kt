package com.alshubaily.chess.server.model

data class Move(val from: Int, val to: Int) {
    fun toUci(): String {
        val files = "abcdefgh"
        val ranks = "12345678"
        val fromFile = files[from % 8]
        val fromRank = ranks[from / 8]
        val toFile = files[to % 8]
        val toRank = ranks[to / 8]
        return "$fromFile$fromRank$toFile$toRank"
    }

    companion object {
        fun fromUci(uci: String): Move {
            val files = "abcdefgh"
            val ranks = "12345678"
            val fromFile = files.indexOf(uci[0])
            val fromRank = ranks.indexOf(uci[1])
            val toFile = files.indexOf(uci[2])
            val toRank = ranks.indexOf(uci[3])
            val from = fromRank * 8 + fromFile
            val to = toRank * 8 + toFile
            return Move(from, to)
        }
    }
}
