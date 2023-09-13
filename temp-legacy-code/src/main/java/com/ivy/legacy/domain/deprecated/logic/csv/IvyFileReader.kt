package com.ivy.wallet.domain.deprecated.logic.csv

import android.content.Context
import android.net.Uri
import com.ivy.legacy.utils.readFile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.charset.Charset
import javax.inject.Inject

class IvyFileReader @Inject constructor(
    @ApplicationContext
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
