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
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.design.l0_system.Theme
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.ui.IvyWebView
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.theme.components.BackButtonType
import com.ivy.wallet.ui.theme.components.IvyToolbar

@SuppressLint("SetJavaScriptEnabled")
@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.WebViewScreen(screen: IvyWebView) {
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
        val ivyContext = ivyWalletCtx()
        val nav = navigation()
        IvyToolbar(
            onBack = { nav.onBackPressed() },
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
                //Activate Dark mode if the user uses Dark theme & it's supported
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    val forceDarkMode = if (ivyContext.theme == Theme.DARK)
                        WebSettingsCompat.FORCE_DARK_ON else WebSettingsCompat.FORCE_DARK_OFF
                    WebSettingsCompat.setForceDark(
                        webView.settings,
                        forceDarkMode
                    )
                }

                //Chrome Client is compatible with most of websites
                webView.webChromeClient = WebChromeClient()
                webView.settings.javaScriptEnabled = true
                webView.loadUrl(url)
            }
        )
    }
}


