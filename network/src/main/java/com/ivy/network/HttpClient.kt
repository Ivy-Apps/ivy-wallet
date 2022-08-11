package com.ivy.network

import com.ivy.frp.monad.Res
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

fun ktorClient(): HttpClient = HttpClient {
    install(Logging)

    install(ContentNegotiation) {
        json()
    }
}

suspend inline fun post(block: HttpRequestBuilder.() -> Unit): Res<HttpResponse, HttpResponse> {
    val response = ktorClient().post {
        contentType(ContentType.Application.Json)
        block()
    }

    return if (response.status.isSuccess()) {
        Res.Ok(response)
    } else {
        Res.Err(response)
    }
}
