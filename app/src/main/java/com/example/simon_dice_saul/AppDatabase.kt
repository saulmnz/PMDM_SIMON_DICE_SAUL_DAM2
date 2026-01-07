package com.example.simon_dice_saul

// 📁 com.example.simon_dice_saul.data.database.AppDatabase.kt

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simon_dice_saul.data.dao.RecordDao
import com.example.simon_dice_saul.data.model.Record

@Database(
    entities = [Record::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}