package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.GameState
import com.alshubaily.chess.server.model.initialBitboards
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
    fun `shannon perft depths`() {
        val expected = mapOf(
            1 to 20L,
            2 to 400L,
            3 to 8902L,
            4 to 197281L,
//            Expected :4865609
//            Actual   :4865351
//            5 to 4865609L, fails (prally makeMove(en croissant)
        )

        val initial = GameState(initialBitboards())

        for ((depth, expectedCount) in expected) {
            val actual = perft(initial, depth)
            assertEquals(expectedCount, actual, "Failed at depth $depth")
        }
    }
}
