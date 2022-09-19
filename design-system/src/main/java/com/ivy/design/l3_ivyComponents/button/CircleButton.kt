package com.ivy.design.l3_ivyComponents.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI

@Deprecated("don't use")
@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    contentDescription: String = "icon",
    backgroundColor: Color = UI.colors.pure,
    borderColor: Color = UI.colors.medium,
    tint: Color? = UI.colors.pureInverse,
    onClick: () -> Unit
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(backgroundColor, CircleShape)
            .border(2.dp, borderColor, CircleShape)
            .padding(6.dp), //enlarge click area
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = tint ?: Color.Unspecified
    )
}