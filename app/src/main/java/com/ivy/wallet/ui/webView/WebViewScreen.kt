package com.ivy.wallet.ui.webView

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.theme.components.BackButtonType
import com.ivy.wallet.ui.theme.components.IvyToolbar

@SuppressLint("SetJavaScriptEnabled")
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.WebViewScreen(screen: Screen.WebView) {
    UI(url = screen.url)
}

@SuppressLint("SetJavaScriptEnabled")
@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(url: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        val ivyContext = LocalIvyContext.current
        IvyToolbar(
            onBack = { ivyContext.onBackPressed() },
            backButtonType = BackButtonType.CLOSE,
            paddingTop = 8.dp,
            paddingBottom = 8.dp
        )

        //Android WebView should not be a in a scroll container :/
        //because anchor links doesn't work
        //https://stackoverflow.com/questions/3039555/android-webview-anchor-link-jump-link-not-working
        AndroidView(
            factory = ::WebView,
            update = { webView ->
                //Chrome Client is compatible with most of websites
                webView.webChromeClient = WebChromeClient()
                webView.settings.javaScriptEnabled = true
                webView.loadUrl(url)
            }
        )
    }
}


