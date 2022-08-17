package com.ivy.core.ui.temp

import android.net.Uri

interface RootScreen {
    fun shareIvyWallet()

    fun openUrlInBrowser(url: String)

    fun openGooglePlayAppPage(appId: String)

    fun reviewIvyWallet(dismissReviewCard: Boolean)

    fun <T> pinWidget(widget: Class<T>)

    fun shareCSVFile(fileUri: Uri)

    fun shareZipFile(fileUri: Uri)
}