package com.ivy.wallet.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.google.accompanist.insets.ProvideWindowInsets
import com.ivy.design.l0_system.Theme
import com.ivy.wallet.ui.theme.IvyTheme


val LocalIvyContext = compositionLocalOf<IvyWalletCtx> { error("No LocalIvyContext") }

@Composable
fun IvyApp(
    ivyContext: IvyWalletCtx,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalIvyContext provides ivyContext,
    ) {
        IvyTheme(theme = ivyContext.theme) {
            Surface(modifier = Modifier.fillMaxSize()) {
                ProvideWindowInsets {
                    BoxWithConstraints {
                        ivyContext.screenWidth = with(LocalDensity.current) { maxWidth.roundToPx() }
                        ivyContext.screenHeight =
                            with(LocalDensity.current) { maxHeight.roundToPx() }

                        content()
                    }
                }
            }
        }
    }

}

@Composable
fun IvyAppPreview(
    theme: Theme = Theme.LIGHT,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    val ivyContext = IvyWalletCtx()
    ivyContext.switchTheme(theme = theme)
    IvyApp(
        ivyContext = ivyContext,
        content = content
    )
}

@Composable
fun ivyContext(): IvyWalletCtx {
    return LocalIvyContext.current
}


