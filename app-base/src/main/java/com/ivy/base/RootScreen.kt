package com.ivy.base

interface RootScreen {
    fun shareIvyWallet()

    fun openUrlInBrowser(url: String)

    fun reviewIvyWallet(dismissReviewCard: Boolean)

    fun <T> pinWidget(widget: Class<T>)
}