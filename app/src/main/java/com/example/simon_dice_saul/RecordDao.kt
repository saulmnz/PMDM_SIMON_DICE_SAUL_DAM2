package com.example.simon_dice_saul
// 📁 com.example.simon_dice_saul.data.dao.RecordDao.kt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.simon_dice_saul.data.model.Record

@Dao
interface RecordDao {

    @Query("SELECT * FROM record_table WHERE id = 0")
    fun getRecord(): Record?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(record: Record)

    @Query("DELETE FROM record_table")
    fun clearRecord()
}