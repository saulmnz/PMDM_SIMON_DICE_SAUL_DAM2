package com.example.simon_dice_saul.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.simon_dice_saul.data.model.Record
import java.text.SimpleDateFormat
import java.util.*

/**
 * OBJETO SINGLETON PARA CONTROLAR LAS PREFERENCIAS COMPARTIDAS.
 * MANEJA EL RÉCORD DE LA APLICACIÓN.
 * DOCUMENTACIÓN: https://developer.android.com/training/data-storage/shared-preferences
 */
object ControladorPreferencias {
    // NOMBRE DEL ARCHIVO DE PREFERENCIAS
    private const val NOMBRE_PREFS = "simon_dice_prefs"
    // CLAVES PARA LOS VALORES GUARDADOS
    private const val CLAVE_RONDA_MAS_ALTA = "ronda_mas_alta"
    private const val CLAVE_FECHA = "fecha_record"

    /**
     * ACTUALIZA EL RÉCORD EN LAS PREFERENCIAS COMPARTIDAS.
     */
    fun actualizarRecord(contexto: Context, rondaMasAlta: Int) {
        val preferenciasCompartidas = contexto.getSharedPreferences(NOMBRE_PREFS, Context.MODE_PRIVATE)

        // GENERAR LA FECHA ACTUAL EN FORMATO STRING
        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        preferenciasCompartidas.edit {
            putInt(CLAVE_RONDA_MAS_ALTA, rondaMasAlta)
            putString(CLAVE_FECHA, fechaActual)
        }
    }

    /**
     * OBTIENE EL RÉCORD GUARDADO EN LAS PREFERENCIAS COMPARTIDAS.
     */
    fun obtenerRecord(contexto: Context): Record? {
        val preferenciasCompartidas = contexto.getSharedPreferences(NOMBRE_PREFS, Context.MODE_PRIVATE)

        return if (preferenciasCompartidas.contains(CLAVE_RONDA_MAS_ALTA)) {
            val ronda = preferenciasCompartidas.getInt(CLAVE_RONDA_MAS_ALTA, 0)
            val fecha = preferenciasCompartidas.getString(CLAVE_FECHA, "") ?: ""

            // SOLO RETORNAR RECORD SI LA FECHA NO ESTÁ VACÍA
            if (fecha.isNotEmpty()) {
                Record(rondaMasAlta = ronda, fecha = fecha)
            } else {
                null
            }
        } else {
            null
        }
    }

    /**
     * ELIMINA TODOS LOS DATOS DE LAS PREFERENCIAS COMPARTIDAS.
     */
    fun limpiarRecord(contexto: Context) {
        val preferenciasCompartidas = contexto.getSharedPreferences(NOMBRE_PREFS, Context.MODE_PRIVATE)
        preferenciasCompartidas.edit {
            clear()
        }
    }
}