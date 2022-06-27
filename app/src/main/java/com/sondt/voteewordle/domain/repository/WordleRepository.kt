package com.sondt.voteewordle.domain.repository

import com.sondt.voteewordle.domain.entity.GuessResult
import io.reactivex.Maybe

interface WordleRepository {
    fun guessRandom(guess: String, size: Int, seed: Int): Maybe<List<GuessResult>>
}
