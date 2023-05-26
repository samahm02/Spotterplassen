package com.example.test.data

import com.example.test.model.PlaneData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*

// DataSourceKtor is a data class to handle HTTP requests with Ktor
class DataSourceKtor(var path: String) {

    // Creating a private client for HTTP requests with Gson content negotiation installed
    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    // Asynchronous function to fetch plane data
    suspend fun fetchFly(): PlaneData {
        return try {
            // Get the request body from the client
            client.get(path).body()
        } catch(e: RedirectResponseException) {
            // Handling 3xx responses
            println("3xx Error: ${e.response.status.description}")
            PlaneData(0, emptyList())
        } catch(e: ClientRequestException) {
            // Handling 4xx responses
            println("4xx Error: ${e.response.status.description}")
            PlaneData(0, emptyList())
        } catch(e: ServerResponseException) {
            // Handling 5xx responses
            println("5xx Error: ${e.response.status.description}")
            PlaneData(0, emptyList())
        } catch(e: Exception) {
            // Handling general errors
            println("General Error: ${e.message}")
            PlaneData(0, emptyList())
        }
    }

    // Asynchronous function to fetch warnings
    suspend fun fetchWarning(): List<Any> {
        return try {
            val builder = HttpRequestBuilder()
            builder.url(path)
            // Adding the API key to the header of the request
            builder.header("X-Gravitee-API-Key", "a7cc3ee4-1921-48b1-b301-40bd185e6b0b")
            // Parsing the warning data
            Warningparser().parse(client.get(builder).body())
        } catch(e: RedirectResponseException) {
            // Handling 3xx responses
            println("3xx Error: ${e.response.status.description}")
            emptyList()
        } catch(e: ClientRequestException) {
            // Handling 4xx responses
            println("4xx Error: ${e.response.status.description}")
            emptyList()
        } catch(e: ServerResponseException) {
            // Handling 5xx responses
            println("5xx Error: ${e.response.status.description}")
            emptyList()
        } catch(e: Exception) {
            // Handling general errors
            println("General Error: ${e.message}")
            emptyList()
        }
    }
}


