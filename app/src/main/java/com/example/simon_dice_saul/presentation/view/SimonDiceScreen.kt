package com.example.simon_dice_saul.presentation.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simon_dice_saul.R
import com.example.simon_dice_saul.data.model.ColorSimon
import com.example.simon_dice_saul.presentation.viewmodel.ModeloVistaSimon

@Composable
fun SimonDiceScreen(
    viewModel: ModeloVistaSimon
) {
    // OBSERVAR EL ESTADO ACTUAL DE LA UI
    val uiState = viewModel.uiState.collectAsState().value
    val eventEffect = viewModel.eventEffect

    LaunchedEffect(eventEffect) {
        if (eventEffect is ModeloVistaSimon.EventEffect.ErrorSound) {
            println("SONIDO DE ERROR ACTIVADO")
            viewModel.consumeEventEffect()
        }
    }

    // DISEÑO PRINCIPAL: COLUMN CENTRADO CON MÁRGENES
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TÍTULO DEL JUEGO
        Text(
            text = "SIMON DICE",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // ESTADO DEL JUEGO (RONDA / PUNTUACIÓN) — EN FILA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = stringResource(R.string.ronda_label, uiState.ronda),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            )
            Text(
                text = stringResource(R.string.puntuacion_label, uiState.puntuacion),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            )
        }

        val statusMessage = when (uiState.gameState) {
            ModeloVistaSimon.GameState.INICIO -> stringResource(R.string.status_inicio)
            ModeloVistaSimon.GameState.JUGANDO -> stringResource(R.string.status_jugando)
            ModeloVistaSimon.GameState.MOSTRANDO_SECUENCIA -> stringResource(R.string.status_mostrando_secuencia)
            ModeloVistaSimon.GameState.ESPERANDO_ENTRADA -> stringResource(R.string.status_esperando_entrada)
            ModeloVistaSimon.GameState.RONDA_SUPERADA -> stringResource(R.string.status_ronda_superada, uiState.ronda - 1)
            ModeloVistaSimon.GameState.ERROR -> stringResource(R.string.status_error)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.animateContentSize()
            )
        }

        // BOTONES DE CONTROL (INICIAR / REINICIAR) — REDONDEADOS Y SUTILES
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.iniciarPartida() },
                enabled = uiState.gameState == ModeloVistaSimon.GameState.INICIO || uiState.gameState == ModeloVistaSimon.GameState.ERROR,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.width(120.dp)
            ) {
                Text(stringResource(R.string.btn_iniciar), fontSize = 16.sp)
            }

            Button(
                onClick = { viewModel.reiniciarJuego() },
                enabled = uiState.gameState == ModeloVistaSimon.GameState.ERROR,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.width(120.dp)
            ) {
                Text(stringResource(R.string.btn_reiniciar), fontSize = 16.sp)
            }
        }

        // CUADRÍCULA DE COLORES — SIN CUADROS, SÓLO CÍRCULOS LLENOS
        ColorButtonsGrid(
            uiState = uiState,
            onColorClick = { color -> viewModel.alPulsarColor(color) }
        )
    }
}

@Composable
private fun ColorButtonsGrid(
    uiState: ModeloVistaSimon.UiState,
    onColorClick: (ColorSimon) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SimonColorCircle(ColorSimon.ROJO, uiState, onColorClick)
            SimonColorCircle(ColorSimon.VERDE, uiState, onColorClick)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SimonColorCircle(ColorSimon.AZUL, uiState, onColorClick)
            SimonColorCircle(ColorSimon.AMARILLO, uiState, onColorClick)
        }
    }
}

@Composable
private fun SimonColorCircle(
    color: ColorSimon,
    uiState: ModeloVistaSimon.UiState,
    onClick: (ColorSimon) -> Unit
) {
    // DEFINIR COLOR DEL FONDO
    val backgroundColor = when (color) {
        ColorSimon.ROJO -> Color(0xFFE53935)
        ColorSimon.VERDE -> Color(0xFF43A047)
        ColorSimon.AZUL -> Color(0xFF1E88E5)
        ColorSimon.AMARILLO -> Color(0xFFFFD600)
    }

    // COLOR DEL TEXTO: AHORA TODOS EN BLANCO → COHERENCIA VISUAL
    val textColor = Color.White

    // ¿ESTÁ RESALTADO? → AUMENTAR TAMAÑO Y SOMBRILLA (OPCIONAL, SI TE GUSTA)
    val isSelected = uiState.highlightedColor == color
    val scale = if (isSelected) 1.1f else 1.0f
    val elevation = if (isSelected) 8.dp else 4.dp

    // BOX PERSONALIZADO — SIN BOTÓN, SÓLO CLICKABLE
    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            //.shadow(elevation)
            .background(backgroundColor)
            .clickable(enabled = uiState.isInputEnabled) { onClick(color) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = color.name.uppercase(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            ),
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.animateContentSize()
        )
    }
}