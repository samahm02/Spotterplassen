package com.example.test.data

import com.example.test.model.FlyData
import com.example.test.model.Warning
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*


class DataSourceFly(var path: String) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun fetchFly(): FlyData {
        return try {
            client.get(path).body()
        } catch(e: RedirectResponseException) {
            //3xx responses
            println("3xx Error: ${e.response.status.description}")
            FlyData(0, emptyList())
        } catch(e: ClientRequestException) {
            //4xx responses
            println("4xx Error: ${e.response.status.description}")
            FlyData(0, emptyList())
        } catch(e: ServerResponseException) {
            //5xx responses
            println("5xx Error: ${e.response.status.description}")
            FlyData(0, emptyList())
        } catch(e: Exception) {
            println("General Error: ${e.message}")
            FlyData(0, emptyList())
        }
    }

    suspend fun fetchWarning(): List<Any> {
        return Warningparser().parse(client.get(path).body())
    }
}


