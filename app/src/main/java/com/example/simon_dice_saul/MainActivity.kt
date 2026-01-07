package com.example.simon_dice_saul

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.simon_dice_saul.data.model.Record
import com.example.simon_dice_saul.presentation.ui.SimonDiceScreen
import com.example.simon_dice_saul.presentation.viewmodel.ModeloVistaSimon
import com.example.simon_dice_saul.ui.theme.SIMON_DICE_SAULTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 Instancia de Room (como en el ejemplo del profe)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "simon_dice_db"
        )
            .allowMainThreadQueries()
            .build()

        val recordDao = db.recordDao()

        // 🔹 Prueba rápida (como en el ejemplo)
        val record = recordDao.getRecord()
        Log.d("ROOMTEST", "Récord inicial: $record")

        // Insertar récord de prueba si no existe (solo primera vez)
        if (record == null) {
            val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            val recordPrueba = Record(rondaMasAlta = 0, fecha = fecha)
            recordDao.insertRecord(recordPrueba)
            Log.d("ROOMTEST", "Récord inicial insertado: $recordPrueba")
        }

        setContent {
            SIMON_DICE_SAULTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: ModeloVistaSimon = viewModel {
                        ModeloVistaSimon(this@MainActivity.application)
                    }
                    SimonDiceScreen(viewModel = viewModel)
                }
            }
        }
    }
}