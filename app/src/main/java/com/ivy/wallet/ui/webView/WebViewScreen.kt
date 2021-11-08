package com.ivy.wallet.ui.webView

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.theme.components.BackButtonType
import com.ivy.wallet.ui.theme.components.IvyToolbar

@SuppressLint("SetJavaScriptEnabled")
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.WebViewScreen(screen: Screen.WebViewScreen) {
    UI(url = screen.url)
}

@SuppressLint("SetJavaScriptEnabled")
@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(url: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        stickyHeader {
            val ivyContext = LocalIvyContext.current
            IvyToolbar(
                onBack = { ivyContext.onBackPressed() },
                backButtonType = BackButtonType.CLOSE
            )
        }

        item {
            AndroidView(
                factory = ::WebView,
                update = { webView ->
                    webView.webViewClient = WebViewClient()
                    webView.settings.javaScriptEnabled = true
                    webView.loadUrl(url)
                }
            )
        }
    }
}


