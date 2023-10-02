package com.ivy.legacy.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.ivy.base.legacy.stringRes
import com.ivy.navigation.navigation
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.Transparent
import com.ivy.wallet.ui.theme.components.CircleButton
import com.ivy.wallet.ui.theme.components.DeleteButton
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton

@Deprecated("Old design system. Use `:ivy-design` and Material3")
@Composable
fun ItemStatisticToolbar(
    contrastColor: Color,

    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        val nav = navigation()
        CircleButton(
            modifier = Modifier.testTag("toolbar_close"),
            icon = R.drawable.ic_dismiss,
            borderColor = contrastColor,
            tint = contrastColor,
            backgroundColor = Transparent
        ) {
            nav.back()
        }

        Spacer(Modifier.weight(1f))

        IvyOutlinedButton(
            iconStart = R.drawable.ic_edit,
            text = stringRes(R.string.edit),
            borderColor = contrastColor,
            iconTint = contrastColor,
            textColor = contrastColor,
            solidBackground = false
        ) {
            onEdit()
        }

        Spacer(Modifier.width(16.dp))

        DeleteButton {
            onDelete()
        }

        Spacer(Modifier.width(24.dp))
    }
}
