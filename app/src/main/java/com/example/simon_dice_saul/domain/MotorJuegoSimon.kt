package com.example.simon_dice_saul.domain

import com.example.simon_dice_saul.data.model.ColorSimon

class MotorJuegoSimon {
    private var numeroRonda = 1
    private var puntuacion = 0
    private var secuencia = mutableListOf<ColorSimon>()
    private var indiceEntradaUsuario = 0

    // ESTADO INTERNO QUE DEVUELVE EL MOTOR (DATOS INMUTABLES)
    data class EstadoMotor(
        val rondaActual: Int,
        val puntuacionActual: Int,
        val secuenciaActual: List<ColorSimon>,
        val indiceComparacion: Int,
        val secuenciaCompletada: Boolean,
        val juegoPerdido: Boolean
    )

    // INICIAR UNA NUEVA PARTIDA DESDE CERO
    fun iniciarPartida(): EstadoMotor {
        numeroRonda = 1
        puntuacion = 0
        secuencia.clear()
        indiceEntradaUsuario = 0
        return obtenerEstadoActual()
    }

    // AÑADIR UN COLOR ALEATORIO A LA SECUENCIA (PARA LA SIGUIENTE RONDA)
    fun anadirColorAleatorio(): EstadoMotor {
        val colorAleatorio = ColorSimon.values().random()
        secuencia.add(colorAleatorio)
        return obtenerEstadoActual()
    }

    // FUNCION QUE VALIDA SI EL COLOR QUE PUSO EL USUARIO ES EL CORRECTO, SI FALLA DEVUELVE JUEGO PERDIDO
    fun validarEntradaUsuario(colorPulsado: ColorSimon): Triple<Boolean, Boolean, Boolean> {
        val colorEsperado = secuencia[indiceEntradaUsuario]
        val esCorrecto = colorPulsado == colorEsperado

        // SI FALLA, PARTIDA TERMINADA
        if (!esCorrecto) {
            return Triple(false, false, true)
        }

        // AVANZAR ÍNDICE DE COMPARACIÓN
        indiceEntradaUsuario++

        // SI SE COMPLETÓ TODA LA SECUENCIA...
        val secuenciaCompletada = indiceEntradaUsuario == secuencia.size
        if (secuenciaCompletada) {
            // AUMENTAR RONDA Y PUNTUACIÓN
            numeroRonda++
            puntuacion += 10 * numeroRonda
            indiceEntradaUsuario = 0
        }

        return Triple(true, secuenciaCompletada, false)
    }

    // OBTENER EL ESTADO ACTUAL DEL MOTOR (SIN MODIFICARLO)
    fun obtenerEstadoActual(): EstadoMotor {
        return EstadoMotor(
            rondaActual = numeroRonda,
            puntuacionActual = puntuacion,
            secuenciaActual = secuencia.toList(),
            indiceComparacion = indiceEntradaUsuario,
            secuenciaCompletada = indiceEntradaUsuario == secuencia.size,
            juegoPerdido = false
        )
    }

    // OBTENER ESTADO CUANDO EL JUEGO HA TERMINADO POR ERROR
    fun obtenerEstadoJuegoTerminado(): EstadoMotor {
        return EstadoMotor(
            rondaActual = numeroRonda,
            puntuacionActual = puntuacion,
            secuenciaActual = secuencia.toList(),
            indiceComparacion = indiceEntradaUsuario,
            secuenciaCompletada = false,
            juegoPerdido = true
        )
    }

    // GETTERS PÚBLICOS (EVITAN ACCESO DIRECTO A VARIABLES INTERNAS)
    fun obtenerPuntuacion(): Int = puntuacion
    fun obtenerRonda(): Int = numeroRonda
    fun obtenerSecuencia(): List<ColorSimon> = secuencia.toList()
}