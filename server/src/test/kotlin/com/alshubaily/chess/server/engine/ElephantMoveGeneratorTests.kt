package com.alshubaily.chess.server.engine

import com.alshubaily.chess.server.model.*
import com.alshubaily.chess.server.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ElephantMoveGeneratorTest {

    @Test
    fun `elephant in center generates full diagonal moves`() {
        val from = index('D', 4)
        val state = GameState(
            board = Bitboards(whiteBishops = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = ElephantMoveGenerator.generate(state)

        assertEquals(
            setOf(
                Move(from, index('C', 3)),
                Move(from, index('B', 2)),
                Move(from, index('A', 1)),
                Move(from, index('E', 5)),
                Move(from, index('F', 6)),
                Move(from, index('G', 7)),
                Move(from, index('H', 8)),

                Move(from, index('C', 5)),
                Move(from, index('B', 6)),
                Move(from, index('A', 7)),

                Move(from, index('E', 3)),
                Move(from, index('F', 2)),
                Move(from, index('G', 1))
            ),
            moves
        )
    }

    @Test
    fun `elephant blocked by own piece stops before it`() {
        val from = index('D', 4)
        val blocker = index('F', 6)
        val state = GameState(
            board = Bitboards(
                whiteBishops = 1L shl from,
                whitePawns = 1L shl blocker
            ),
            currentPlayer = Player.WHITE
        )

        val moves = ElephantMoveGenerator.generate(state)

        assert(!moves.contains(Move(from, blocker)))
        assert(!moves.contains(Move(from, index('G', 7))))
    }

    @Test
    fun `elephant can capture enemy piece but not beyond`() {
        val from = index('D', 4)
        val target = index('F', 6)
        val state = GameState(
            board = Bitboards(
                whiteBishops = 1L shl from,
                blackPawns = 1L shl target
            ),
            currentPlayer = Player.WHITE
        )

        val moves = ElephantMoveGenerator.generate(state)

        assert(moves.contains(Move(from, target)))
        assert(!moves.contains(Move(from, index('G', 7))))
    }

    @Test
    fun `elephant in corner limited to one diagonal`() {
        val from = index('A', 1)
        val state = GameState(
            board = Bitboards(whiteBishops = 1L shl from),
            currentPlayer = Player.WHITE
        )

        val moves = ElephantMoveGenerator.generate(state)

        assertEquals(
            setOf(
                Move(from, index('B', 2)),
                Move(from, index('C', 3)),
                Move(from, index('D', 4)),
                Move(from, index('E', 5)),
                Move(from, index('F', 6)),
                Move(from, index('G', 7)),
                Move(from, index('H', 8))
            ),
            moves
        )
    }
}
