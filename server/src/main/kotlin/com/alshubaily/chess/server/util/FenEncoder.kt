package com.alshubaily.chess.server.util

import com.alshubaily.chess.server.model.CastlingRight
import com.alshubaily.chess.server.model.GameState
import com.alshubaily.chess.server.model.Player
import kotlin.experimental.or

object FenEncoder {

    val pieceToPlane = mapOf(
        'P' to 0, 'N' to 1, 'B' to 2, 'R' to 3, 'Q' to 4, 'K' to 5,
        'p' to 6, 'n' to 7, 'b' to 8, 'r' to 9, 'q' to 10, 'k' to 11
    )

    fun encodeBoardFeatures(fen: String): ByteArray {
        val parts = fen.split(" ")
        val piecePlacement = parts[0]
        val activeColor = parts[1]
        val castling = parts[2]
        val enPassant = parts[3]

        val planes = buildPiecePlanes(piecePlacement)
        val bytes = ByteArray(98)

        var bit = 0
        for (plane in planes)
            for (row in plane)
                for (cell in row) {
                    if (cell == 1)
                        bytes[bit / 8] = bytes[bit / 8] or (1 shl (bit % 8)).toByte()
                    bit++
                }

        writeSideToMove(activeColor, bytes)
        writeCastlingRights(castling, bytes)
        writeEnPassantFile(enPassant, bytes)

        return bytes
    }

    private fun buildPiecePlanes(fenPlacement: String): Array<Array<IntArray>> {
        val planes = Array(12) { Array(8) { IntArray(8) } }
        val rows = fenPlacement.split("/")

        for ((rank, row) in rows.withIndex()) {
            var file = 0
            for (char in row) {
                if (char.isDigit()) {
                    file += char.digitToInt()
                } else {
                    pieceToPlane[char]?.let { plane ->
                        planes[plane][rank][file] = 1
                    }
                    file++
                }
            }
        }
        return planes
    }

    private fun writeSideToMove(activeColor: String, bytes: ByteArray) {
        if (activeColor == "w") {
            val bit = 768
            bytes[bit / 8] = bytes[bit / 8] or (1 shl (bit % 8)).toByte()
        }
    }

    private fun writeCastlingRights(castling: String, bytes: ByteArray) {
        val castlingBitOffsets = mapOf(
            'K' to 769,
            'Q' to 770,
            'k' to 771,
            'q' to 772
        )
        for ((char, bit) in castlingBitOffsets) {
            if (char in castling) {
                bytes[bit / 8] = bytes[bit / 8] or (1 shl (bit % 8)).toByte()
            }
        }
    }

    private fun writeEnPassantFile(enPassant: String, bytes: ByteArray) {
        if (enPassant != "-") {
            val file = enPassant[0] - 'a'
            if (file in 0..7) {
                val bit = 773 + file
                bytes[bit / 8] = bytes[bit / 8] or (1 shl (bit % 8)).toByte()
            }
        }
    }



    fun encode(state: GameState): String {
        val board = state.board
        val builder = StringBuilder()

        for (rank in 7 downTo 0) {
            var empty = 0
            for (file in 0 until 8) {
                val square = rank * 8 + file
                val piece = pieceAt(board, square)

                if (piece != null) {
                    if (empty > 0) {
                        builder.append(empty)
                        empty = 0
                    }
                    builder.append(piece)
                } else {
                    empty++
                }
            }
            if (empty > 0) builder.append(empty)
            if (rank > 0) builder.append('/')
        }

        val turn = if (state.currentPlayer == Player.WHITE) "w" else "b"
        val castling = encodeCastlingRights(state.castlingRights)
        val enPassant = encodeEnPassant(state.enCroissantSquare)

        return "$builder $turn $castling $enPassant 0 1"
    }

    private fun encodeCastlingRights(castlingRights: Set<CastlingRight>): String {
        if (castlingRights.isEmpty()) return "-"
        val builder = StringBuilder()
        if (CastlingRight.WHITE_KINGSIDE in castlingRights) builder.append('K')
        if (CastlingRight.WHITE_QUEENSIDE in castlingRights) builder.append('Q')
        if (CastlingRight.BLACK_KINGSIDE in castlingRights) builder.append('k')
        if (CastlingRight.BLACK_QUEENSIDE in castlingRights) builder.append('q')
        return builder.toString()
    }

    private fun encodeEnPassant(enCroissantSquare: Int?): String {
        if (enCroissantSquare == null) return "-"
        val file = enCroissantSquare % 8
        val rank = enCroissantSquare / 8
        val fileChar = "abcdefgh"[file]
        val rankChar = "12345678"[rank]
        return "$fileChar$rankChar"
    }

}
