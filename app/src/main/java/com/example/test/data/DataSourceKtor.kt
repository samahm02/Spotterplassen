package com.example.test.data

import com.example.test.model.FlyData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*

// DataSourceKtor class responsible for fetching data using Ktor client
class DataSourceKtor(var path: String) {

    // Create an instance of HttpClient with ContentNegotiation plugin configured to use Gson
    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    // Fetches fly data from the specified path
    suspend fun fetchFly(): FlyData {
        return try {
            client.get(path).body() // Perform a GET request to the path and deserialize the response to FlyData
        } catch(e: RedirectResponseException) {
            // Handle 3xx responses (redirects)
            println("3xx Error: ${e.response.status.description}")
            FlyData(0, emptyList())
        } catch(e: ClientRequestException) {
            // Handle 4xx responses (client errors)
            println("4xx Error: ${e.response.status.description}")
            FlyData(0, emptyList())
        } catch(e: ServerResponseException) {
            // Handle 5xx responses (server errors)
            println("5xx Error: ${e.response.status.description}")
            FlyData(0, emptyList())
        } catch(e: Exception) {
            // Handle other exceptions
            println("General Error: ${e.message}")
            FlyData(0, emptyList())
        }
    }

    // Fetches warning data from the specified path and returns a list of warnings
    suspend fun fetchWarning(): List<Any> {
        val builder = HttpRequestBuilder()
        builder.url(path)
        builder.header("X-Gravitee-API-Key", "a7cc3ee4-1921-48b1-b301-40bd185e6b0b")
        return Warningparser().parse(client.get(builder).body()) // Perform a GET request with the specified headers and deserialize the response using Warningparser
    }
}
