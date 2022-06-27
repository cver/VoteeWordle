package com.sondt.voteewordle.data.local.dao

import androidx.room.*
import com.sondt.voteewordle.data.local.entity.WordEntity
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface WordDao {
    @Query("SELECT * FROM word ORDER BY word ASC")
    fun getAll(): List<WordEntity>

    @Query("SELECT COUNT(word) FROM word")
    fun getRowCount(): Maybe<Int>

    @Query("SELECT COUNT(word) FROM word WHERE word = :word")
    fun countWord(word: String): Maybe<Int>

    @Query("SELECT * FROM word WHERE word GLOB :glob ORDER BY word ASC")
    fun find(glob: String): Maybe<List<WordEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg words: WordEntity)

    @Delete
    fun delete(word: WordEntity)

    @Query("DELETE FROM word")
    fun deleteAll()
}
