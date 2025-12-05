package com.example.simon_dice_saul.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.simon_dice_saul.domain.MotorJuegoSimon
import com.example.simon_dice_saul.data.model.ColorSimon
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import com.example.simon_dice_saul.data.model.Record
import com.example.simon_dice_saul.data.repository.ControladorPreferencias

class ModeloVistaSimon(
    aplicacion: Application,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    private val skipDelaysForTest: Boolean = false
) : AndroidViewModel(aplicacion) {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // FLUJO PARA EVENTOS DE UN SOLO USO (SONIDOS, TOAST, NAVIGACIÓN)
    private val _eventEffect = MutableStateFlow<EventEffect?>(null)
    val eventEffect: StateFlow<EventEffect?>
        get() = _eventEffect.asStateFlow()

    // FLUJO PARA EL RÉCORD
    private val _estadoRecord = MutableStateFlow<Record?>(null)
    val estadoRecord: StateFlow<Record?> = _estadoRecord.asStateFlow()

    // CONSUMIR EL EVENTO PARA EVITAR REPETICIONES EN RECOMPOSICIONES
    fun consumeEventEffect() {
        _eventEffect.value = null
    }

    // INSTANCIA DEL MOTOR DE JUEGO
    private val motorJuego = MotorJuegoSimon()

    // VARIABLES INTERNAS PARA CONTROLAR LA ANIMACIÓN DE LA SECUENCIA
    private var indiceSecuenciaActual = 0
    private var estaMostrandoSecuencia = false

    init {
        cargarRecord()
    }

    /**
     * CARGA EL RÉCORD GUARDADO DESDE LAS PREFERENCIAS COMPARTIDAS.
     */
    private fun cargarRecord() {
        _estadoRecord.value = ControladorPreferencias.obtenerRecord(getApplication())
    }

    // INICIAR UNA NUEVA PARTIDA DESDE CERO
    fun iniciarPartida() {
        val estado = motorJuego.iniciarPartida()
        updateUiState {
            copy(
                gameState = GameState.INICIO,
                ronda = estado.rondaActual,
                puntuacion = estado.puntuacionActual,
                isInputEnabled = false,
                highlightedColor = null
            )
        }
        // INICIAR LA PRIMERA SECUENCIA AUTOMÁTICAMENTE
        generarYMostrarSecuencia()
    }

    // GENERAR UN NUEVO COLOR Y MOSTRAR LA SECUENCIA COMPLETA
    private fun generarYMostrarSecuencia() {
        val estado = motorJuego.anadirColorAleatorio()
        updateUiState {
            copy(
                gameState = GameState.MOSTRANDO_SECUENCIA,
                ronda = estado.rondaActual,
                puntuacion = motorJuego.obtenerPuntuacion(),
                isInputEnabled = false,
                highlightedColor = null
            )
        }

        estaMostrandoSecuencia = true
        indiceSecuenciaActual = 0
        mostrarSiguienteColor(estado.secuenciaActual)
    }

    // MOSTRAR CADA COLOR DE LA SECUENCIA CON UN RETRASO (600ms)
    private fun mostrarSiguienteColor(secuencia: List<ColorSimon>) {
        if (indiceSecuenciaActual >= secuencia.size) {
            // SECUENCIA TERMINADA → PASAR A ESPERAR LA ENTRADA DEL JUGADOR
            estaMostrandoSecuencia = false
            updateUiState {
                copy(
                    gameState = GameState.ESPERANDO_ENTRADA,
                    isInputEnabled = true,
                    highlightedColor = null
                )
            }
            return
        }

        // RESALTAR EL COLOR ACTUAL EN LA SECUENCIA
        val colorAResaltar = secuencia[indiceSecuenciaActual]
        updateUiState {
            copy(highlightedColor = colorAResaltar)
        }

        if (skipDelaysForTest) {
            indiceSecuenciaActual++
            mostrarSiguienteColor(secuencia)
            return
        }

        viewModelScope.launch(dispatcher) {
            delay(600L)
            indiceSecuenciaActual++
            mostrarSiguienteColor(secuencia)
        }
    }

    /**
     * ACTUALIZA EL RÉCORD SI LA RONDA ACTUAL SUPERA EL RÉCORD ANTERIOR.
     */
    private fun actualizarRecordSiEsNecesario(rondaActual: Int) {
        val recordActual = _estadoRecord.value

        // DEBUG: Verificar valores
        println("DEBUG ModeloVistaSimon: Record actual = $recordActual, Ronda actual = $rondaActual")

        // SOLO ACTUALIZAR SI NO HAY RÉCORD O SI LA RONDA ES MAYOR
        if (recordActual == null || rondaActual > recordActual.rondaMasAlta) {
            println("DEBUG ModeloVistaSimon: ¡Actualizando récord!")

            // ¡AQUÍ ESTÁ LA CLAVE! La fecha se genera DENTRO de actualizarRecord
            ControladorPreferencias.actualizarRecord(getApplication(), rondaActual)

            // CARGAR EL NUEVO RÉCORD
            cargarRecord()
        } else {
            println("DEBUG ModeloVistaSimon: No se actualiza, récord actual es mayor o igual")
        }
    }

    // MANEJAR LA PULSACIÓN DE UN BOTÓN DE COLOR POR PARTE DEL USUARIO
    fun alPulsarColor(color: ColorSimon) {
        if (estaMostrandoSecuencia) return

        // VALIDAR LA ENTRADA CON EL MOTOR DE JUEGO
        val (esCorrecto, estaCompletada, esJuegoTerminado) = motorJuego.validarEntradaUsuario(color)

        if (esJuegoTerminado) {
            // PARTIDA TERMINADA POR ERROR
            val estadoFinal = motorJuego.obtenerEstadoJuegoTerminado()

            println("DEBUG ModeloVistaSimon: Juego terminado en ronda ${estadoFinal.rondaActual}")

            updateUiState {
                copy(
                    gameState = GameState.ERROR,
                    ronda = estadoFinal.rondaActual,
                    puntuacion = estadoFinal.puntuacionActual,
                    isInputEnabled = false,
                    highlightedColor = null
                )
            }

            // ACTUALIZAR RÉCORD SI ES NECESARIO
            actualizarRecordSiEsNecesario(estadoFinal.rondaActual)

            // EMITIR EVENTO DE SONIDO DE ERROR
            _eventEffect.value = EventEffect.ErrorSound
            return
        }

        if (estaCompletada) {
            // RONDA SUPERADA → GENERAR SIGUIENTE SECUENCIA TRAS UN BREVE RETRASO
            val estado = motorJuego.obtenerEstadoActual()
            updateUiState {
                copy(
                    gameState = GameState.RONDA_SUPERADA,
                    ronda = estado.rondaActual,
                    puntuacion = estado.puntuacionActual,
                    isInputEnabled = false,
                    highlightedColor = null
                )
            }
            if (skipDelaysForTest) {
                generarYMostrarSecuencia()
            } else {
                viewModelScope.launch(dispatcher) {
                    delay(1500L)
                    generarYMostrarSecuencia()
                }
            }
        } else {
            // ENTRADA CORRECTA PERO SEC. INCOMPLETA → SEGUIR ESPERANDO
            updateUiState {
                copy(highlightedColor = null)
            }
        }
    }

    // REINICIAR EL JUEGO DESDE LA PANTALLA DE ERROR
    fun reiniciarJuego() {
        iniciarPartida()
    }

    // ACTUALIZAR EL ESTADO DE FORMA SEGURA Y ATÓMICA
    private fun updateUiState(update: UiState.() -> UiState) {
        _uiState.value = _uiState.value.update()
    }

    // CLASE SEALADA PARA MODELAR TODOS LOS ESTADOS POSIBLES DEL JUEGO
    sealed class GameState {
        object INICIO : GameState()
        object JUGANDO : GameState()
        object MOSTRANDO_SECUENCIA : GameState()
        object ESPERANDO_ENTRADA : GameState()
        object RONDA_SUPERADA : GameState()
        object ERROR : GameState()
    }

    // ESTADO INMUTABLE QUE LA UI OBSERVA
    data class UiState(
        val gameState: GameState = GameState.INICIO,
        val ronda: Int = 0,
        val puntuacion: Int = 0,
        val isInputEnabled: Boolean = false,
        val highlightedColor: ColorSimon? = null
    )

    // EVENTOS DE UN SOLO USO
    sealed class EventEffect {
        object ErrorSound : EventEffect()
    }

    // MÉTODOS PARA TESTING
    fun paraTest_actualizarRecordSiEsNecesario(rondaActual: Int) {
        actualizarRecordSiEsNecesario(rondaActual)
    }

    fun paraTest_establecerEstadoRecord(record: Record?) {
        _estadoRecord.value = record
    }
}