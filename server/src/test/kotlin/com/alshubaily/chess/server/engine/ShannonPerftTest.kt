package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.GameState
import com.alshubaily.chess.server.model.initialBitboards
import com.alshubaily.chess.server.model.initialGameState
import com.alshubaily.chess.server.util.makeMove
import kotlin.test.Test
import kotlin.test.assertEquals

class ShannonPerftTest {

    private fun perft(state: GameState, depth: Int): Long {
        if (depth == 0) return 1
        return MoveGenerator.generateLegalMoves(state).sumOf { move ->
            perft(makeMove(state, move), depth - 1)
        }
    }

    @Test
    fun `shannon perft tests`() {
        val expected = mapOf(
            1 to 20L,
            2 to 400L,
            3 to 8_902L,
            4 to 197_281L,
            5 to 4_865_609L,
            6 to 119_060_324L,
//            7 to 3_195_901_860,
        )

        val state = initialGameState()
        for ((depth, expectedCount) in expected) {
            val actual = perft(state, depth)
            assertEquals(expectedCount, actual, "Failed at depth $depth")
        }
    }
}
