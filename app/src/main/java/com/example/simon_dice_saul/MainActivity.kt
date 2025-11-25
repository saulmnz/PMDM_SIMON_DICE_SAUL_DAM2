package com.example.simon_dice_saul

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simon_dice_saul.presentation.ui.SimonDiceScreen
import com.example.simon_dice_saul.presentation.viewmodel.ModeloVistaSimon
import com.example.simon_dice_saul.ui.theme.SIMON_DICE_SAULTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SIMON_DICE_SAULTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SimonDiceScreen(
                        viewModel = viewModel()
                    )
                }
            }
        }
    }
}