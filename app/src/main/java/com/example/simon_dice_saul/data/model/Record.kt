package com.example.simon_dice_saul.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record_table")
data class Record(
    @PrimaryKey val id: Int = 0,
    val rondaMasAlta: Int,
    val fecha: String
)