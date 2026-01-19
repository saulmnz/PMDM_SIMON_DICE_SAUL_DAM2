// 📁 com/example/simon_dice_saul/data/repository/MongoApiRepository.kt
package com.example.simon_dice_saul.data.repository

import com.example.simon_dice_saul.data.model.Record
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RecordDto(
    val rondaMasAlta: Int,
    val fecha: String
)

class MongoApiRepository(
    private val apiUrl: String = "http://172.20.10.2:3000" // ← Tu IP de VM
) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun saveRecord(record: Record) {
        withContext(Dispatchers.IO) {
            try {
                val dto = RecordDto(
                    rondaMasAlta = record.rondaMasAlta,
                    fecha = record.fecha
                )
                client.post("$apiUrl/record") {
                    contentType(io.ktor.http.ContentType.Application.Json)
                    setBody(dto)
                }
                println("DEBUG: Récord enviado a API REST → MongoDB")
            } catch (e: Exception) {
                println("ERROR al enviar a API: ${e.message}")
            }
        }
    }
}