package com.ivy.wallet.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.wallet.ui.theme.gradientCutBackgroundBottom

enum class BackButtonType {
    BACK, CLOSE
}

@Composable
fun IvyToolbar(
    onBack: () -> Unit,
    backButtonType: BackButtonType = BackButtonType.BACK,
    Content: @Composable RowScope.() -> Unit = { }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .gradientCutBackgroundBottom(paddingBottom = 16.dp)
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        when (backButtonType) {
            BackButtonType.BACK -> {
                BackButton {
                    onBack()
                }
            }
            BackButtonType.CLOSE -> {
                CloseButton {
                    onBack()
                }
            }
        }

        Content()
    }
}