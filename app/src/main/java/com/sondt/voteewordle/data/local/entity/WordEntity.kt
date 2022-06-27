package com.sondt.voteewordle.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sondt.voteewordle.domain.entity.Word

@Entity(tableName = "word")
data class WordEntity(
    @PrimaryKey @ColumnInfo(name = "word") val word: String,
) {
    fun toEntity(): Word = Word(word = word)
}
