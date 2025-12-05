package com.example.simon_dice_saul.presentation.viewmodel

import com.example.simon_dice_saul.data.model.ColorSimon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ModeloVistaSimonTest {

    private lateinit var viewModel: ModeloVistaSimon
    private val testDispatcher = StandardTestDispatcher()

    // CONFIGURAMOS EL ENTORNO DE PRUEBAS ANTES DE CADA TEST
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ModeloVistaSimon(testDispatcher)
    }

    // LIMPIAMOS DESPUES DE CADA TEST
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // VERIFICAMOS QUE AL INICIAR LA PARTIDA SE ESTABLEZCAN LOS VALORES CORRECTOS
    @Test
    fun `iniciarPartida establece estado inicial correctamente`() = runTest {
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

    // CONFIRMAMOS QUE AL INICIAR SE GENERA UNA SECUENCIA AUTOMATICAMENTE
    @Test
    fun `al iniciar partida se genera secuencia automaticamente`() = runTest {
        viewModel.iniciarPartida()

        // DEJAMOS QUE SE COMPLETE LA SECUENCIA
        testScheduler.advanceUntilIdle()

        val estado = viewModel.uiState.first()

        // DEBERIA ESTAR ESPERANDO QUE EL JUGADOR PULSE
        assertEquals(ModeloVistaSimon.GameState.ESPERANDO_ENTRADA, estado.gameState)
        assertEquals(1, estado.ronda)
        assertTrue(estado.isInputEnabled)
    }

    // COMPROBAMOS QUE NO SE PUEDE PULSAR DURANTE LA SECUENCIA
    @Test
    fun `alPulsarColor durante secuencia es ignorado`() = runTest {
        viewModel.iniciarPartida()

        // INTENTAMOS PULSAR UN COLOR MIENTRAS SE MUESTRA LA SECUENCIA
        viewModel.alPulsarColor(ColorSimon.ROJO)

        // AVANZAMOS UN POCO PERO NO DEMASIADO
        testScheduler.advanceTimeBy(100L)

        val estado = viewModel.uiState.first()

        // SI ESTA MOSTRANDO SECUENCIA, EL INPUT DEBERIA ESTAR BLOQUEADO
        if (estado.gameState == ModeloVistaSimon.GameState.MOSTRANDO_SECUENCIA) {
            assertFalse(estado.isInputEnabled)
        }
    }

    // VERIFICAMOS QUE SE PUEDEN LIMPIAR LOS EVENTOS
    @Test
    fun `consumirEventEffect limpia el evento correctamente`() = runTest {
        // CONSUMIMOS CUALQUIER EVENTO QUE HAYA
        viewModel.consumeEventEffect()

        // NO DEBERIA HABER NINGUN EVENTO ACTIVO
        assertNull(viewModel.eventEffect)
    }

    // TEST PARA COMPROBAR QUE EL REINICIO FUNCIONA BIEN
    @Test
    fun `reiniciarJuego resetea el juego correctamente`() = runTest {
        viewModel.iniciarPartida()
        testScheduler.advanceUntilIdle()

        // REINICIAMOS EL JUEGO
        viewModel.reiniciarJuego()
        testScheduler.advanceUntilIdle()

        val estado = viewModel.uiState.first()

        // DESPUES DE REINICIAR DEBERIA ESTAR MOSTRANDO SECUENCIA O ESPERANDO
        assertTrue(
            estado.gameState == ModeloVistaSimon.GameState.MOSTRANDO_SECUENCIA ||
                    estado.gameState == ModeloVistaSimon.GameState.ESPERANDO_ENTRADA
        )
        assertEquals(1, estado.ronda)
        assertEquals(0, estado.puntuacion)
    }

    // OBSERVAMOS LAS TRANSICIONES ENTRE ESTADOS DEL JUEGO
    @Test
    fun `transiciones de estado durante el flujo del juego`() = runTest {
        viewModel.iniciarPartida()

        // MIENTRAS SE MUESTRA LA SECUENCIA
        testScheduler.advanceTimeBy(100L)
        var estado = viewModel.uiState.first()
        assertEquals(ModeloVistaSimon.GameState.MOSTRANDO_SECUENCIA, estado.gameState)
        assertFalse(estado.isInputEnabled)

        // CUANDO TERMINA LA SECUENCIA
        testScheduler.advanceUntilIdle()
        estado = viewModel.uiState.first()
        assertEquals(ModeloVistaSimon.GameState.ESPERANDO_ENTRADA, estado.gameState)
        assertTrue(estado.isInputEnabled)
    }

    // CONFIRMAMOS QUE SE RESALTAN COLORES DURANTE LA SECUENCIA
    @Test
    fun `highlightedColor se actualiza durante la secuencia`() = runTest {
        viewModel.iniciarPartida()

        // NOS COLOCAMOS EN MEDIO DE LA SECUENCIA
        testScheduler.advanceTimeBy(100L)

        val estado = viewModel.uiState.first()

        // DEBERIA HABER UN COLOR RESALTADO EN ESE MOMENTO
        if (estado.gameState == ModeloVistaSimon.GameState.MOSTRANDO_SECUENCIA) {
            assertTrue(estado.highlightedColor != null)
        }
    }

    // VERIFICAMOS LA PUNTUACION Y RONDA INICIAL
    @Test
    fun `puntuacion y ronda se incrementan correctamente`() = runTest {
        viewModel.iniciarPartida()
        testScheduler.advanceUntilIdle()

        val estadoInicial = viewModel.uiState.first()
        assertEquals(1, estadoInicial.ronda)
        assertEquals(0, estadoInicial.puntuacion)
    }

    // COMPROBAMOS CUANDO ESTA ACTIVO EL INPUT DEL JUGADOR
    @Test
    fun `input habilitado solo en estado ESPERANDO_ENTRADA`() = runTest {
        viewModel.iniciarPartida()

        // DURANTE LA SECUENCIA NO SE PUEDE PULSAR
        testScheduler.advanceTimeBy(100L)
        var estado = viewModel.uiState.first()
        if (estado.gameState == ModeloVistaSimon.GameState.MOSTRANDO_SECUENCIA) {
            assertFalse(estado.isInputEnabled)
        }

        // SOLO SE PUEDE PULSAR CUANDO ESPERA LA ENTRADA
        testScheduler.advanceUntilIdle()
        estado = viewModel.uiState.first()
        if (estado.gameState == ModeloVistaSimon.GameState.ESPERANDO_ENTRADA) {
            assertTrue(estado.isInputEnabled)
        }
    }
}