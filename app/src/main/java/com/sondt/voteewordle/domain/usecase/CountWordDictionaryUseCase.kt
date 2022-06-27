package com.sondt.voteewordle.domain.usecase

import com.sondt.voteewordle.domain.repository.WordDictRepository
import io.reactivex.Maybe

class CountWordDictionaryUseCase(private val wordDictRepository: WordDictRepository) {
    operator fun invoke(): Maybe<Int> {
        return wordDictRepository.count()
    }
}
