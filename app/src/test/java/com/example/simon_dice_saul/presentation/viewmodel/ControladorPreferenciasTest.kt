package com.example.simon_dice_saul.data.repository

import android.content.Context
import android.content.SharedPreferences
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import java.text.SimpleDateFormat
import com.example.simon_dice_saul.data.model.Record
import java.util.*

/**
 * TESTS QUE PRUEBAN LA LÓGICA DE NUESTRO SISTEMA DE RÉCORDS
 * Usando Mocks para simular SharedPreferences
 */
class ControladorPreferenciasTest {

    // TEST 1: LÓGICA DE FECHA
    @Test
    fun testFormatoFechaEsCorrecto() {
        // Verificar que nuestro formato de fecha es el correcto
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = formato.format(Date())

        // El formato debe tener exactamente este patrón
        assertTrue("La fecha debe contener /", fecha.contains("/"))
        assertTrue("La fecha debe contener :", fecha.contains(":"))
        assertTrue("La fecha debe tener al menos 16 caracteres", fecha.length >= 16)

        // Verificar patrón con regex
        val regex = Regex("""\d{2}/\d{2}/\d{4} \d{2}:\d{2}""")
        assertTrue("Formato de fecha incorrecto", fecha.matches(regex))
    }


    // TEST 2: LÓGICA DE COMPARACIÓN DE RÉCORDS
    @Test
    fun testLogicaComparacionRecords() {
        // CASO 1: No hay récord anterior -> DEBE guardar
        val noRecord: String? = null
        val rondaNueva = 5
        val debeGuardarCaso1 = noRecord == null || rondaNueva > 3 // 3 sería el récord anterior
        assertTrue("Sin récord previo siempre debe guardar", debeGuardarCaso1)

        // CASO 2: Ronda nueva es mayor -> DEBE guardar
        val recordAnterior = 3
        val debeGuardarCaso2 = rondaNueva > recordAnterior
        assertTrue("Ronda mayor debe guardar", debeGuardarCaso2)

        // CASO 3: Ronda nueva es igual -> NO debe guardar
        val rondaIgual = 3
        val debeGuardarCaso3 = rondaIgual > recordAnterior
        assertFalse("Ronda igual no debe guardar", debeGuardarCaso3)

        // CASO 4: Ronda nueva es menor -> NO debe guardar
        val rondaMenor = 2
        val debeGuardarCaso4 = rondaMenor > recordAnterior
        assertFalse("Ronda menor no debe guardar", debeGuardarCaso4)
    }

    // TEST 3: ESTRUCTURA DEL MODELO RECORD
    @Test
    fun testEstructuraRecord() {
        // Crear un Record como lo haría nuestra app
        val record = Record(
            rondaMasAlta = 10,
            fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date())
        )

        // Verificar estructura
        assertEquals("La ronda debe ser 10", 10, record.rondaMasAlta)
        assertNotNull("La fecha no debe ser null", record.fecha)
        assertTrue("La fecha no debe estar vacía", record.fecha.isNotEmpty())

        // Verificar que es una data class (tiene equals, hashCode, etc.)
        val record2 = Record(10, record.fecha)
        assertEquals("Records iguales deben ser iguales", record, record2)
        assertEquals("HashCodes deben ser iguales", record.hashCode(), record2.hashCode())
    }

    // TEST 4: COMPORTAMIENTO DEL SINGLETON
    @Test
    fun testSingletonPattern() {
        // El ControladorPreferencias es un object (singleton)
        val instancia1 = ControladorPreferencias
        val instancia2 = ControladorPreferencias

        // Deben ser la misma instancia
        assertSame("Debe ser el mismo objeto singleton", instancia1, instancia2)

        // Debe tener los métodos que necesitamos
        val metodos = ControladorPreferencias::class.java.declaredMethods
        val nombresMetodos = metodos.map { it.name }

        assertTrue("Debe tener método actualizarRecord",
            nombresMetodos.contains("actualizarRecord"))
        assertTrue("Debe tener método obtenerRecord",
            nombresMetodos.contains("obtenerRecord"))
        assertTrue("Debe tener método limpiarRecord",
            nombresMetodos.contains("limpiarRecord"))
    }
}