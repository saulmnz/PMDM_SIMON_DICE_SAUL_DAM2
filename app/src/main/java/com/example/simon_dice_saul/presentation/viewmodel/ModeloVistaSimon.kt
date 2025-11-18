package com.example.simon_dice_saul.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.simon_dice_saul.domain.MotorJuegoSimon
import com.example.simon_dice_saul.data.model.ColorSimon

// VIEWMODEL -> INTERMEDIARIO ENTRE LA LÓGICA Y LA INTERFAZ
class ModeloVistaSimon : ViewModel() {
    private val _estadoInterfaz = MutableLiveData<EstadoInterfaz>()
    val estadoInterfaz: LiveData<EstadoInterfaz> = _estadoInterfaz
    private val motorJuego = MotorJuegoSimon()

    // VARIABLES PARA CONTROLAR LA ANIMACIÓN DE LA SECUENCIA
    private var índiceSecuenciaActual = 0
    private var estáMostrandoSecuencia = false

    // INICIA UNA NUEVA PARTIDA
    fun iniciarPartida() {
        val estado = motorJuego.iniciarPartida()
        _estadoInterfaz.value = EstadoInterfaz.Inicio
        _estadoInterfaz.value = EstadoInterfaz.Jugando(
            ronda = estado.rondaActual,
            puntuación = estado.puntuaciónActual,
            secuencia = emptyList(),
            entradaHabilitada = false
        )
        generarYMostrarSecuencia()
    }

    // GENERA UN NUEVO COLOR Y MUESTRA LA SECUENCIA COMPLETA
    private fun generarYMostrarSecuencia() {
        val estado = motorJuego.añadirColorAleatorio()
        _estadoInterfaz.value = EstadoInterfaz.MostrandoSecuencia(
            ronda = estado.rondaActual,
            puntuación = estado.puntuaciónActual,
            secuencia = estado.secuenciaActual,
            índiceResaltado = -1  // PARA NO RESALTAR NINGÚN BOTÓN AL PRINCIPIO
        )

        estáMostrandoSecuencia = true
        índiceSecuenciaActual = 0
        mostrarSiguienteColor(estado.secuenciaActual)
    }

    // ILUMINA CADA BOTÓN EN ORDEN
    private fun mostrarSiguienteColor(secuencia: List<ColorSimon>) {
        if (índiceSecuenciaActual >= secuencia.size) {
            // SECUENCIA TERMINADA → PASAR A ESPERAR ENTRADA DEL USUARIO
            estáMostrandoSecuencia = false
            _estadoInterfaz.value = EstadoInterfaz.EsperandoEntrada(
                ronda = secuencia.size,
                puntuación = motorJuego.javaClass.getDeclaredField("puntuación").get(motorJuego) as Int,
                secuencia = secuencia,
                entradaHabilitada = true
            )
            return
        }

        val colorAResaltar = secuencia[índiceSecuenciaActual]
        _estadoInterfaz.value = EstadoInterfaz.MostrandoSecuencia(
            ronda = secuencia.size,
            puntuación = motorJuego.javaClass.getDeclaredField("puntuación").get(motorJuego) as Int,
            secuencia = secuencia,
            índiceResaltado = índiceSecuenciaActual
        )

        viewModelScope.launch {
            delay(600)
            índiceSecuenciaActual++
            mostrarSiguienteColor(secuencia)
        }
    }

    // MÉTODO LLAMADO CUANDO EL USUARIO PULSA UN BOTÓN DE COLOR
    fun alPulsarColor(color: ColorSimon) {
        if (estáMostrandoSecuencia) return  // NO PERMITIR ENTRADA DURANTE LA ANIMACIÓN !!!!!!!!!

        val (esCorrecto, estáCompletada, esJuegoTerminado) = motorJuego.validarEntradaUsuario(color)

        if (esJuegoTerminado) {
            val estadoFinal = motorJuego.obtenerEstadoJuegoTerminado()
            _estadoInterfaz.value = EstadoInterfaz.JuegoTerminado(
                rondaFinal = estadoFinal.rondaActual,
                puntuaciónFinal = estadoFinal.puntuaciónActual
            )
            return
        }

        if (estáCompletada) {
            // RONDA SUPERADA → MOSTRAR MENSAJE Y GENERAR SIGUIENTE SECUENCIA
            val estado = motorJuego.obtenerEstadoActual()
            _estadoInterfaz.value = EstadoInterfaz.RondaSuperada(
                ronda = estado.rondaActual,
                puntuación = estado.puntuaciónActual
            )
            generarYMostrarSecuencia()
        }
        // SI ES CORRECTO PERO AÚN NO SE COMPLETÓ, SEGUIR EN "EsperandoEntrada" (SIN CAMBIAR ESTADO)
    }

    // REINICIA EL JUEGO DESDE LA PANTALLA DE GAME OVER
    fun reiniciarJuego() {
        iniciarPartida()
    }

    // ESTADOS DE LA INTERFAZ (SEALED CLASS)
    sealed class EstadoInterfaz {
        // PANTALLA INICIAL
        object Inicio : EstadoInterfaz()

        // JUEGO EN CURSO, PERO NO MOSTRANDO NI ESPERANDO
        data class Jugando(
            val ronda: Int,
            val puntuación: Int,
            val secuencia: List<ColorSimon>,
            val entradaHabilitada: Boolean
        ) : EstadoInterfaz()

        // MOSTRANDO LA SECUENCIA
        data class MostrandoSecuencia(
            val ronda: Int,
            val puntuación: Int,
            val secuencia: List<ColorSimon>,
            val índiceResaltado: Int
        ) : EstadoInterfaz()

        // ESPERANDO QUE EL USUARIO PULSE LOS BOTONES
        data class EsperandoEntrada(
            val ronda: Int,
            val puntuación: Int,
            val secuencia: List<ColorSimon>,
            val entradaHabilitada: Boolean
        ) : EstadoInterfaz()

        // EL USUARIO COMPLETÓ LA SECUENCIA
        data class RondaSuperada(
            val ronda: Int,
            val puntuación: Int
        ) : EstadoInterfaz()

        // EL USUARIO FALLÓ → GAME OVER
        data class JuegoTerminado(
            val rondaFinal: Int,
            val puntuaciónFinal: Int
        ) : EstadoInterfaz()
    }
}