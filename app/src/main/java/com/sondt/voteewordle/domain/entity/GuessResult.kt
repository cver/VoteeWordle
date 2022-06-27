package com.sondt.voteewordle.domain.entity

data class GuessResult(
    val slot: Int,
    val guess: String,
    val result: GuessResultType,
)
