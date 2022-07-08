package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ivy.design.l0_system.UI
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.Transparent
import com.ivy.wallet.utils.thenIf


@Composable
fun BoxWithConstraintsScope.GradientCutBottom(
    height: Dp = 96.dp,
    alpha: Float = 1f,
    zIndex: Float? = null
) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .thenIf(zIndex != null) {
                zIndex(zIndex!!)
            }
            .background(Gradient(Transparent, UI.colors.pure).asVerticalBrush())
            .align(Alignment.BottomCenter)
            .alpha(alpha = alpha)
    )
}

@Composable
fun BoxWithConstraintsScope.GradientCutTop(
    modifier: Modifier = Modifier,
    height: Dp,
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(Gradient(UI.colors.pure, Transparent).asVerticalBrush())
            .align(Alignment.TopCenter)
    )
}