package com.sondt.voteewordle.domain.usecase

import com.sondt.voteewordle.domain.repository.WordDictRepository
import io.reactivex.Completable

class SyncWordDictionaryUseCase(private val wordDictRepository: WordDictRepository) {
    operator fun invoke(): Completable {
        return wordDictRepository.downloadAndSaveWordDict()
    }
}