package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.wallet.ui.theme.gradientCutBackgroundTop
import com.ivy.wallet.utils.navigationBarInset
import com.ivy.wallet.utils.toDensityDp

@Composable
fun BoxWithConstraintsScope.BackBottomBar(
    bottomInset: Dp = navigationBarInset().toDensityDp(),
    onBack: () -> Unit,
    PrimaryAction: @Composable () -> Unit,
) {
    ActionsRow(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .gradientCutBackgroundTop()
            .padding(bottom = bottomInset)
            .padding(bottom = 16.dp)
    ) {
        Spacer(Modifier.width(20.dp))

        CircleButton(
            modifier = Modifier.rotate(180f),
            icon = R.drawable.ic_arrow_right
        ) {
            onBack()
        }

        Spacer(Modifier.weight(1f))

        PrimaryAction()

        Spacer(Modifier.width(20.dp))
    }
}