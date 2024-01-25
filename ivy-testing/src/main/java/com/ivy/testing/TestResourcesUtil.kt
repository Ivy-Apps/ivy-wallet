package com.ivy.testing

import android.net.Uri

fun Any.testResourceUri(resPath: String): Uri {
    try {
        val fileUrl = this::class.java.classLoader!!.getResource(resPath)
        return Uri.parse(fileUrl.toString())
    } catch (e: Exception) {
        throw TestResourceLoadException(resPath, e)
    }
}

class TestResourceLoadException(resPath: String, e: Throwable?) :
    Exception("Could not load \"$resPath\" test resource. Check your test setup.", e)