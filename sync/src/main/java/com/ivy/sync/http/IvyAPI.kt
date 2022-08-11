package com.ivy.sync.http

import com.ivy.frp.monad.Res
import com.ivy.network.post
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend inline fun <reified T> postIvy(
    endpoint: String,
    body: T
): Res<HttpResponse, HttpResponse> = post {
    url {
        host = IvyServer.HOST
        path(endpoint)
    }
    setBody(body)
}