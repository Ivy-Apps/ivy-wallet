package com.ivy.design.l2_components.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.White
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.data.Background
import com.ivy.design.l1_buildingBlocks.data.applyBackground
import com.ivy.design.l1_buildingBlocks.data.clipBackground
import com.ivy.design.l1_buildingBlocks.data.solid
import com.ivy.design.l1_buildingBlocks.hapticClickable
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.padding

@Suppress("unused")
@Composable
fun Btn.Icon(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    iconTint: Color = White,
    background: Background = solid(
        color = UI.colors.primary,
        shape = CircleShape,
        padding = padding(all = 8.dp)
    ),
    contentDescription: String = "icon button",
    hapticFeedback: Boolean = false,
    onClick: () -> Unit
) {
    IconRes(
        modifier = modifier
            .clipBackground(background)
            .hapticClickable(hapticFeedbackEnabled = hapticFeedback, onClick = onClick)
            .applyBackground(background),
        icon = icon,
        tint = iconTint,
        contentDescription = contentDescription,
    )
}

@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        Btn.Icon(
            icon = R.drawable.ic_popup_add,
            modifier = Modifier.size(48.dp),
            background = solid(
                CircleShape, UI.colors.primary, padding(all = 12.dp)
            )
        ) {

        }
    }
}