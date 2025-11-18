package com.example.simon_dice_saul.domain
import kotlin.random.Random
import com.example.simon_dice_saul.data.model.ColorSimon

// MOTOR DEL JUEGO -> LÓGICA
class MotorJuegoSimón {
    private var numeroRonda = 1
    private var puntuación = 0
    private var secuencia = mutableListOf<ColorSimon>()
    private var índiceEntradaUsuario = 0

    // ESTADO INTERNO QUE DEVUELVE EL MOTOR
    data class EstadoMotor(
        val rondaActual: Int,
        val puntuaciónActual: Int,
        val secuenciaActual: List<ColorSimon>,
        val índiceComparación: Int,
        val secuenciaCompletada: Boolean,
        val juegoPerdido: Boolean
    )

    // MÉTODO PARA INICIAR UNA NUEVA PARTIDA DESDE CERO
    fun iniciarPartida(): EstadoMotor {
        numeroRonda = 1
        puntuación = 0
        secuencia.clear()
        índiceEntradaUsuario = 0
        return obtenerEstadoActual()
    }

    // MÉTODO QUE AÑADE UN COLOR ALEATORIO A LA SECUENCIA (PARA LA SIGUIENTE RONDA)
    fun añadirColorAleatorio(): EstadoMotor {
        val colorAleatorio = ColorSimon.values().random()
        secuencia.add(colorAleatorio)
        return obtenerEstadoActual()
    }

    // VALIDA LA ENTRADA DEL USUARIO Y DEVUELVE BOOLEANOS PARA VALIDAR LA SECUENCIA
    fun validarEntradaUsuario(colorPulsado: ColorSimon): Triple<Boolean, Boolean, Boolean> {
        val colorEsperado = secuencia[índiceEntradaUsuario]
        val esCorrecto = colorPulsado == colorEsperado

        // SI FALLA, PARTIDA TERMINADA
        if (!esCorrecto) {
            return Triple(false, false, true)
        }

        // AVANZAR ÍNDICE
        índiceEntradaUsuario++

        // SI COMPLETÓ TODA LA SECUENCIA...
        val secuenciaCompletada = índiceEntradaUsuario == secuencia.size
        if (secuenciaCompletada) {
            // AUMENTA LA RONDA Y PUNTUACIÓN
            numeroRonda++
            puntuación += 10 * numeroRonda
            índiceEntradaUsuario = 0
        }

        return Triple(true, secuenciaCompletada, false)
    }

    // OBTIENE EL ESTADO ACTUAL DEL MOTOR (PARA EL VIEWMODEL)
    private fun obtenerEstadoActual(): EstadoMotor {
        return EstadoMotor(
            rondaActual = numeroRonda,
            puntuaciónActual = puntuación,
            secuenciaActual = secuencia.toList(),
            índiceComparación = índiceEntradaUsuario,
            secuenciaCompletada = índiceEntradaUsuario == secuencia.size,
            juegoPerdido = false
        )
    }

    // ESTADO CUANDO EL JUEGO HA TERMINADO POR ERROR
    fun obtenerEstadoJuegoTerminado(): EstadoMotor {
        return EstadoMotor(
            rondaActual = numeroRonda,
            puntuaciónActual = puntuación,
            secuenciaActual = secuencia.toList(),
            índiceComparación = índiceEntradaUsuario,
            secuenciaCompletada = false,
            juegoPerdido = true
        )
    }
}