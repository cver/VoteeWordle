package com.sondt.voteewordle.domain.repository

import com.sondt.voteewordle.data.local.entity.WordEntity
import io.reactivex.Completable
import io.reactivex.Maybe

interface WordDictRepository {
    fun downloadAndSaveWordDict(): Completable
    fun count(): Maybe<Int>
    fun hasExisted(word: String): Maybe<Boolean>
    fun searchWords(glob: String): Maybe<List<WordEntity>>
}
