package com.example.simon_dice_saul.data.model;

enum class EstadoJuego {
    INICIO,
    JUGANDO,
    MOSTRANDO_SECUENCIA,
    ESPERANDO_ENTRADA,
    VERIFICANDO_ENTRADA,
    RONDA_SUPERADA,
    JUEGO_TERMINADO
}

enum class ColorSimon(val identificador: Int) {
    ROJO(0), VERDE(1), AZUL(2), AMARILLO(3)
}