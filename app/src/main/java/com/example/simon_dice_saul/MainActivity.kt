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
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SIMON_DICE_SAULTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: ModeloVistaSimon = viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return ModeloVistaSimon(this@MainActivity.application) as T
                            }
                        }
                    )
                    SimonDiceScreen(viewModel = viewModel)
                }
            }
        }
    }
}