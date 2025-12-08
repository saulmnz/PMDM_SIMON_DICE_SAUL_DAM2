package com.example.simon_dice_saul.data.model

/**
 * MODELO DE DATOS QUE REPRESENTA EL RÉCORD DEL JUEGO.
 * CONTIENE LA RONDA MÁS ALTA ALCANZADA Y LA FECHA COMO STRING.
 * DOCUMENTACIÓN: https://developer.android.com/training/data-storage/shared-preferences#kotlin
 */
data class Record(
    val rondaMasAlta: Int,
    val fecha: String
)