package com.sondt.voteewordle.data.repository

import android.util.Log
import com.sondt.voteewordle.data.local.dao.WordDao
import com.sondt.voteewordle.data.local.entity.WordEntity
import com.sondt.voteewordle.data.remote.WordDictService
import com.sondt.voteewordle.domain.repository.WordDictRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.*

class WordDictRepositoryImpl(
    private val wordDictService: WordDictService,
    private val wordDao: WordDao,
    private val internalFileDir: File,
): WordDictRepository {

    override fun downloadAndSaveWordDict(): Completable {
        // download file from https://raw.githubusercontent.com/mwiens91/english-words-py/master/english_words/__init__.py
        // which are using for k2bd/wordle-api service
        return wordDictService.downloadEnglishWordsFile()
            .doOnNext {
                val fileName = "word-dict.txt"
                val file = File(internalFileDir, fileName)
                if (file.exists()) {
                    file.delete()
                }

                val fo = FileOutputStream(file)
                // store file to internal directory
                fo.write(it.bytes())
                fo.close()

                val fi = FileInputStream(file)

                val beginLine = "english_words_lower_alpha_set = {"
                val endLine = "}"
                var shouldSave = false

                wordDao.deleteAll()

                BufferedReader(InputStreamReader(fi)).useLines { instance ->
                    // select correct line and insert to SQLite
                    instance.iterator().forEach { line ->
                        if (line == beginLine) {
                            shouldSave = true
                        } else if (line == endLine) {
                            shouldSave = false
                        } else if (shouldSave) {
                            val word = line.replace(",", "").replace("'", "").trim()
                            if (word.length == 5) {
                                wordDao.insertAll(WordEntity(word = word))
                                print(word)
                            }
                        }
                    }
                }
                fi.close()

                if (file.exists()) {
                    // delete after insert
                    file.delete()
                }

                print("done")
            }
            .ignoreElements()
    }

    override fun count(): Maybe<Int> {
        return wordDao.getRowCount()
    }

    override fun hasExisted(word: String): Maybe<Boolean> {
        return wordDao.countWord(word).map {
            it == 1
        }
    }

    override fun searchWords(glob: String): Maybe<List<WordEntity>> {
        return wordDao.find(glob)
    }
}
