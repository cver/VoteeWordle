package com.sondt.voteewordle.domain.usecase

import com.sondt.voteewordle.domain.entity.GuessResult
import com.sondt.voteewordle.domain.repository.WordDictRepository
import com.sondt.voteewordle.domain.repository.WordleRepository
import io.reactivex.Maybe

class GuessRandomFirstWordUseCase(
    private val wordDictRepository: WordDictRepository,
    private val wordleRepository: WordleRepository,
) {
    operator fun invoke(seed: Int, startWord: String): Maybe<List<GuessResult>> {
        return wordDictRepository.hasExisted(startWord)
            .flatMap {
               if (it) {
                   wordleRepository.guessRandom(startWord, 5, seed)
               } else {
                   Maybe.error(Throwable("invalid_word"))
               }
            }
    }
}
