package com.ivy.design.api

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ivy.design.IvyContext
import com.ivy.design.l0_system.IvyTheme
import com.ivy.design.l0_system.Theme

val LocalIvyContext = compositionLocalOf<IvyContext> { error("No LocalIvyContext") }

@Composable
fun IvyUI(
    design: IvyDesign,
    Content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    val ivyContext = design.context()

    CompositionLocalProvider(
        LocalIvyContext provides ivyContext,
    ) {
        IvyTheme(
            theme = ivyContext.theme,
            design = design
        ) {

            val systemUiController = rememberSystemUiController()
            // TODO: Check if this m3 migration is correct
            val useDarkIcons = ivyContext.theme == Theme.LIGHT

            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons
                )
            }

            Surface(modifier = Modifier.fillMaxSize()) {
                ProvideWindowInsets {
                    BoxWithConstraints {
                        ivyContext.screenWidth = with(LocalDensity.current) {
                            maxWidth.roundToPx()
                        }
                        ivyContext.screenHeight = with(LocalDensity.current) {
                            maxHeight.roundToPx()
                        }


                        Content()
                    }
                }
            }
        }
    }
}

@Composable
fun ivyContext(): IvyContext {
    return LocalIvyContext.current
}