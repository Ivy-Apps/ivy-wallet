package com.ivy.core

import android.net.Uri
import java.util.UUID

interface RootScreen {
    /**
     * BuildConfig.DEBUG
     */
    val isDebug: Boolean

    /**
     * BuildConfig.VERSION_NAME
     */
    val buildVersionName: String

    /**
     * BuildConfig.VERSION_CODE
     */
    val buildVersionCode: Int

    fun reviewIvyWallet(dismissReviewCard: Boolean)

    fun shareIvyWallet()

    fun openUrlInBrowser(url: String)

    fun shareCSVFile(fileUri: Uri)

    fun shareZipFile(fileUri: Uri)

    fun openGooglePlayAppPage(appId: String)

    fun pinWidget(widget: Class<*>)
}