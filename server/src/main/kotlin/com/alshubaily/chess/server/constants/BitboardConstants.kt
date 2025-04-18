package com.alshubaily.chess.server.constants

const val RANK_2: Long = 0x000000000000FF00L
const val RANK_7: Long = 0x00FF000000000000L

const val FILE_A: Long = (0L
        or (1L shl 0) or (1L shl 8) or (1L shl 16) or (1L shl 24)
        or (1L shl 32) or (1L shl 40) or (1L shl 48) or (1L shl 56)
        )

const val FILE_H: Long = (0L
        or (1L shl 7) or (1L shl 15) or (1L shl 23) or (1L shl 31)
        or (1L shl 39) or (1L shl 47) or (1L shl 55) or (1L shl 63)
        )

const val FORWARD = 8
const val FORWARD_LEFT = 7
const val FORWARD_RIGHT = 9
