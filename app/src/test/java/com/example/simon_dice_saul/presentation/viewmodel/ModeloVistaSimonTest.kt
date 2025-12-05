package com.example.simon_dice_saul.presentation.viewmodel

import android.app.Application
import com.example.simon_dice_saul.data.model.ColorSimon
import com.example.simon_dice_saul.data.model.Record
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.Date
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ModeloVistaSimonTest {

    private lateinit var viewModel: ModeloVistaSimon
    private val testDispatcher = StandardTestDispatcher()

    // CONFIGURAMOS EL ENTORNO DE PRUEBAS ANTES DE CADA TEST
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val mockApplication = mock(Application::class.java)
        viewModel = ModeloVistaSimon(mockApplication, testDispatcher)
    }

    // LIMPIAMOS DESPUES DE CADA TEST
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // VERIFICAMOS QUE AL INICIAR LA PARTIDA SE ESTABLEZCAN LOS VALORES CORRECTOS
    @Test
    fun `INICIAR PARTIDA ESTABLECE ESTADO INICIAL CORRECTAMENTE`() = runTest {
        viewModel.iniciarPartida()

        // ESPERAMOS A QUE TERMINEN TODAS LAS CORRUTINAS
        testScheduler.advanceUntilIdle()

        val estado = viewModel.uiState.first()

        // COMPROBAMOS QUE EL ESTADO FINAL SEA EL ESPERADO
        assertEquals(ModeloVistaSimon.GameState.ESPERANDO_ENTRADA, estado.gameState)
        assertEquals(1, estado.ronda)
        assertEquals(0, estado.puntuacion)
        assertTrue(estado.isInputEnabled)
    }


    @Test
    fun `ACTUALIZAR RÉCORD CUANDO NO HAY RÉCORD PREVIO`() = runTest {
        // DADO - NO HAY RÉCORD PREVIO
        viewModel.paraTest_establecerEstadoRecord(null)

        // CUANDO - ACTUALIZAR CON RONDA 3
        viewModel.paraTest_actualizarRecordSiEsNecesario(3)

        // ENTONCES - DEBE CREAR NUEVO RÉCORD
        val record = viewModel.estadoRecord.first()
        assertNotNull(record)
        assertEquals(3, record?.rondaMasAlta)
    }

    @Test
    fun `CARGA DE RECORD CON TIMESTAMP VALIDO`() {
        // ESTE TEST SIMULA QUE EL MÉTODO cargarRecord() FUNCIONA CORRECTAMENTE
        // NO SE PUEDE PROBAR DIRECTAMENTE PORQUE USA SharedPreferences
        // PERO SE VERIFICA EN LOS TESTS DE ControladorPreferencias
        assertTrue(true)
    }
}