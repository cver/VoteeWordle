package com.sondt.voteewordle.data.repository

import com.sondt.voteewordle.data.remote.WordleService
import com.sondt.voteewordle.domain.entity.GuessResult
import com.sondt.voteewordle.domain.repository.WordleRepository
import io.reactivex.Maybe

class WordleRepositoryImpl(
    private val wordleService: WordleService,
): WordleRepository {

    override fun guessRandom(guess: String, size: Int, seed: Int): Maybe<List<GuessResult>> {
        return wordleService.guessRandom(guess, size, seed)
    }
}
