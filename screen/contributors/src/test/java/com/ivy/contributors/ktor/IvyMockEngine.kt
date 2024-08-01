package com.ivy.contributors.ktor

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

class IvyMockEngine {
    fun get(): HttpClientEngine = client.engine

    private val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
    private val client = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                if (request.url.toString() == FIRST_PAGE) {
                    respond(contributors, HttpStatusCode.OK, responseHeaders)
                } else if (request.url.toString() == SECOND_PAGE) {
                    respond(emptyResult, HttpStatusCode.OK, responseHeaders)
                } else {
                    error("Error")
                }
            }
        }
    }

    companion object {
        private const val FIRST_PAGE = "https://api.github.com/repos/Ivy-Apps/ivy-wallet/contributors?page=1"
        private const val SECOND_PAGE = "https://api.github.com/repos/Ivy-Apps/ivy-wallet/contributors?page=2"
    }

    private val emptyResult = """[]"""
    private val contributors = """[
{
    "login": "a_login",
    "avatar_url": "a_avatar",
    "html_url": "a_link",
    "contributions": 685
},
{
    "login": "b_login",
    "avatar_url": "b_avatar",
    "html_url": "b_link",
    "contributions": 101
}
]"""
}