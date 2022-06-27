package com.sondt.voteewordle

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sondt.voteewordle.data.local.dao.WordDao
import com.sondt.voteewordle.data.local.entity.WordEntity

@Database(entities = [WordEntity::class], version = 1)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
