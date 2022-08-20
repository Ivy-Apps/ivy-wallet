package com.ivy.core.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.functions.account.dummyAcc
import com.ivy.core.ui.R
import com.ivy.core.ui.color.contrastColor
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.data.account.Account
import com.ivy.data.icon.IconSize
import com.ivy.data.icon.IvyIcon
import com.ivy.design.l0_system.*
import com.ivy.design.l1_buildingBlocks.IvyText
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.utils.ComponentPreview
import com.ivy.design.utils.thenIf

@Composable
fun Account.Badge(
    background: Color = color.toComposeColor(),
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .background(background, UI.shapes.rFull)
            .thenIf(onClick != null) {
                clip(UI.shapes.rFull)
                    .clickable(onClick = onClick!!)
            }
            .padding(start = 8.dp, end = 18.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contrastColor = background.contrastColor()

        icon.ItemIcon(
            size = IconSize.S,
            tint = contrastColor,
        )
        SpacerHor(width = 4.dp)


        IvyText(
            text = name,
            typo = UI.typo.c.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}

@Preview
@Composable
private fun Preview_Black() {
    ComponentPreview {
        dummyAcc(
            name = "Cash",
            icon = IvyIcon.Sized(
                iconS = R.drawable.ic_custom_account_s,
                iconM = 0,
                iconL = 0,
                iconId = null
            )
        ).Badge(
            background = Black
        )
    }
}

@Preview
@Composable
private fun Preview_Color() {
    ComponentPreview {
        dummyAcc(
            name = "Cash",
            icon = IvyIcon.Sized(
                iconS = R.drawable.ic_custom_account_s,
                iconM = 0,
                iconL = 0,
                iconId = null
            ),
            color = Green.toArgb(),
        ).Badge()
    }
}