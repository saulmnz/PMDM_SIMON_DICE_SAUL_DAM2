package com.example.simon_dice_saul.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.simon_dice_saul.data.database.AppDatabase
import com.example.simon_dice_saul.data.model.Record
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.simon_dice_saul.domain.MotorJuegoSimon
import com.example.simon_dice_saul.data.model.ColorSimon
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.*

class ModeloVistaSimon(
    aplicacion: Application,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    private val skipDelaysForTest: Boolean = false
) : AndroidViewModel(aplicacion) {

    // 🔹 Instancia de Room (como en el ejemplo del profe)
    private val db = Room.databaseBuilder(
        aplicacion,
        AppDatabase::class.java,
        "simon_dice_db"
    )
        .allowMainThreadQueries()  // ⚠️ Solo para simpleza (no en producción grande)
        .build()
    private val recordDao = db.recordDao()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _eventEffect = MutableStateFlow<EventEffect?>(null)
    val eventEffect: StateFlow<EventEffect?>
        get() = _eventEffect.asStateFlow()

    private val _estadoRecord = MutableStateFlow<Record?>(null)
    val estadoRecord: StateFlow<Record?> = _estadoRecord.asStateFlow()

    fun consumeEventEffect() {
        _eventEffect.value = null
    }

    private val motorJuego = MotorJuegoSimon()
    private var indiceSecuenciaActual = 0
    private var estaMostrandoSecuencia = false

    init {
        // 🔹 Cargar récord desde Room (no desde SharedPreferences)
        _estadoRecord.value = recordDao.getRecord()
    }

    private fun cargarRecord() {
        // Ya no se usa → el init lo hace directamente
    }

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
        generarYMostrarSecuencia()
    }

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

    private fun mostrarSiguienteColor(secuencia: List<ColorSimon>) {
        if (indiceSecuenciaActual >= secuencia.size) {
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
     * ACTUALIZA EL RÉCORD SI LA RONDA ACTUAL ES MAYOR.
     */
    private fun actualizarRecordSiEsNecesario(rondaActual: Int) {
        val recordActual = _estadoRecord.value

        println("DEBUG ModeloVistaSimon: Record actual = $recordActual, Ronda actual = $rondaActual")

        if (recordActual == null || rondaActual > recordActual.rondaMasAlta) {
            println("DEBUG ModeloVistaSimon: ¡Nuevo récor!")

            val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            val nuevoRecord = Record(
                id = 0,
                rondaMasAlta = rondaActual,
                fecha = fecha
            )

            recordDao.insertRecord(nuevoRecord)
            _estadoRecord.value = nuevoRecord  // Actualiza UI
        }
    }

    fun alPulsarColor(color: ColorSimon) {
        if (estaMostrandoSecuencia) return

        val (esCorrecto, estaCompletada, esJuegoTerminado) = motorJuego.validarEntradaUsuario(color)

        if (esJuegoTerminado) {
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

            actualizarRecordSiEsNecesario(estadoFinal.rondaActual)
            _eventEffect.value = EventEffect.ErrorSound
            return
        }

        if (estaCompletada) {
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
            updateUiState {
                copy(highlightedColor = null)
            }
        }
    }

    fun reiniciarJuego() {
        iniciarPartida()
    }

    private fun updateUiState(update: UiState.() -> UiState) {
        _uiState.value = _uiState.value.update()
    }

    sealed class GameState {
        object INICIO : GameState()
        object JUGANDO : GameState()
        object MOSTRANDO_SECUENCIA : GameState()
        object ESPERANDO_ENTRADA : GameState()
        object RONDA_SUPERADA : GameState()
        object ERROR : GameState()
    }

    data class UiState(
        val gameState: GameState = GameState.INICIO,
        val ronda: Int = 0,
        val puntuacion: Int = 0,
        val isInputEnabled: Boolean = false,
        val highlightedColor: ColorSimon? = null
    )

    sealed class EventEffect {
        object ErrorSound : EventEffect()
    }

    fun paraTest_actualizarRecordSiEsNecesario(rondaActual: Int) {
        actualizarRecordSiEsNecesario(rondaActual)
    }

    fun paraTest_establecerEstadoRecord(record: Record?) {
        _estadoRecord.value = record
    }
}