package com.ivy.design.api

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.ivy.design.IvyContext
import com.ivy.design.l0_system.IvyTheme

val LocalIvyContext = compositionLocalOf<IvyContext> { error("No LocalIvyContext") }

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun IvyUI(
    design: IvyDesign,
    includeSurface: Boolean = true,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) {
    val ivyContext = design.context()

    CompositionLocalProvider(
        LocalIvyContext provides ivyContext,
    ) {
        IvyTheme(
            theme = ivyContext.theme,
            design = design
        ) {
            WrapWithSurface(includeSurface = includeSurface) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    ivyContext.screenWidth = with(LocalDensity.current) {
                        maxWidth.roundToPx()
                    }
                    ivyContext.screenHeight = with(LocalDensity.current) {
                        maxHeight.roundToPx()
                    }

                    content()
                }
            }
        }
    }
}

@Composable
private fun WrapWithSurface(
    includeSurface: Boolean,
    content: @Composable () -> Unit,
) {
    if (includeSurface) {
        Surface {
            content()
        }
    } else {
        content()
    }
}

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun ivyContext(): IvyContext {
    return LocalIvyContext.current
}
