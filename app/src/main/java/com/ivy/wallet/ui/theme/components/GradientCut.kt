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
import com.ivy.wallet.base.thenIf
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.IvyTheme
import com.ivy.wallet.ui.theme.Transparent

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
            .background(Gradient(Transparent, IvyTheme.colors.pure).asVerticalBrush())
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
            .background(Gradient(IvyTheme.colors.pure, Transparent).asVerticalBrush())
            .align(Alignment.TopCenter)
    )
}