package com.example.test.data

import com.example.test.model.FlyData
import com.example.test.model.Warning
import io.ktor.client.*
import io.ktor.client.call.*
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
        return client.get(path).body()
    }

    suspend fun fetchWarning(): List<Any> {
        return Warningparser().parse(client.get(path).body())
    }
}


