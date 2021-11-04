package com.ivy.wallet.logic.csv

import android.content.Context
import android.net.Uri
import com.ivy.wallet.base.readFile
import java.nio.charset.Charset

class IvyFileReader(
    private val appContext: Context
) {

    fun read(
        uri: Uri,
        charset: Charset = Charsets.UTF_8
    ): String? {
        return readFile(
            context = appContext,
            uri = uri,
            charset = charset
        )
    }
}