package com.ivy.network

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.gson.*

fun ktorClient(): HttpClient = HttpClient {
    install(Logging)

    install(ContentNegotiation) {
        gson()
    }
}