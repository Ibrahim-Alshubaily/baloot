package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.model.Bitboards
import com.alshubaily.chess.server.model.GameState
import com.alshubaily.chess.server.model.Player

object FenParser {

    fun parse(fen: String): GameState {
        val (boardPart, turnPart) = fen.split(" ")
        val bitboards = parseBoard(boardPart)
        val currentPlayer = if (turnPart == "w") Player.WHITE else Player.BLACK
        return GameState(bitboards, currentPlayer)
    }

    private fun parseBoard(boardPart: String): Bitboards {
        val rows = boardPart.split("/")
        val squares = mutableMapOf<Int, Char>()
        var square = 56

        for (row in rows) {
            var col = 0
            for (char in row) {
                col += if (char.isDigit()) {
                    char.digitToInt()
                } else {
                    squares[square + col] = char
                    1
                }
            }
            square -= 8
        }

        return Bitboards(
            whitePawns = squares.toBitboard('P'),
            whiteKnights = squares.toBitboard('N'),
            whiteBishops = squares.toBitboard('B'),
            whiteRooks = squares.toBitboard('R'),
            whiteQueens = squares.toBitboard('Q'),
            whiteKing = squares.toBitboard('K'),
            blackPawns = squares.toBitboard('p'),
            blackKnights = squares.toBitboard('n'),
            blackBishops = squares.toBitboard('b'),
            blackRooks = squares.toBitboard('r'),
            blackQueens = squares.toBitboard('q'),
            blackKing = squares.toBitboard('k')
        )
    }

    private fun Map<Int, Char>.toBitboard(piece: Char): Long {
        return entries
            .filter { it.value == piece }
            .fold(0L) { acc, (pos, _) -> acc or (1L shl pos) }
    }
}