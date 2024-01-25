package com.ivy.testing

import android.net.Uri

fun testResourceUri(resPath: String): Uri {
    try {
        val classLoader = Thread.currentThread().contextClassLoader
        val fileUrl = classLoader!!.getResource(resPath)
            ?: throw TestResourceLoadException(resPath, message = "The fileUrl is null!")
        return Uri.parse(fileUrl.toString())
    } catch (e: Exception) {
        throw TestResourceLoadException(resPath, e)
    }
}

class TestResourceLoadException(
    resPath: String,
    e: Throwable? = null,
    message: String = "Check your test setup.",
) : Exception("Could not load \"$resPath\" test resource. $message", e)