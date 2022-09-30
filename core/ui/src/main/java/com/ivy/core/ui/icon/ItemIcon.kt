package com.ivy.core.ui.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.util.ComponentPreview

@Composable
fun ItemIcon(
    icon: ItemIcon,
    size: IconSize,
    tint: Color = UI.colorsInverted.pure
) {
    when (icon) {
        is ItemIcon.Sized -> when (size) {
            IconSize.S -> IconRes(
                modifier = Modifier
                    .size(size.toDp()),
                icon = icon.iconS,
                tint = tint,
            )
            IconSize.M -> IconRes(
                modifier = Modifier
                    .size(size.toDp()),
                icon = icon.iconM,
                tint = tint,
            )
            IconSize.L -> IconRes(
                modifier = Modifier
                    .size(size.toDp()),
                icon = icon.iconL,
                tint = tint,
            )
        }
        is ItemIcon.Unknown -> Image(
            modifier = Modifier
                .size(size.toDp())
                .padding(all = 4.dp),
            painter = painterResource(icon.icon),
            contentDescription = "itemIcon",
            colorFilter = ColorFilter.tint(tint),
            alignment = Alignment.Center,
            contentScale = ContentScale.FillBounds,
        )
    }
}

@Composable
private fun IconSize.toDp(): Dp = remember(this) {
    when (this) {
        IconSize.S -> 32.dp
        IconSize.M -> 48.dp
        IconSize.L -> 64.dp
    }
}

//region Previews
@Preview
@Composable
private fun Preview_Sized_S() {
    ComponentPreview {
        ItemIcon(
            icon = ItemIcon.Sized(
                iconS = R.drawable.ic_custom_account_s,
                iconM = 0,
                iconL = 0,
                iconId = null
            ),
            size = IconSize.S
        )
    }
}

@Preview
@Composable
private fun Preview_Sized_M() {
    ComponentPreview {
        ItemIcon(
            ItemIcon.Sized(
                iconS = 0,
                iconM = R.drawable.ic_custom_account_m,
                iconL = 0,
                iconId = null
            ),
            size = IconSize.M
        )
    }
}

@Preview
@Composable
private fun Preview_Sized_L() {
    ComponentPreview {
        ItemIcon(
            icon = ItemIcon.Sized(
                iconS = 0,
                iconM = 0,
                iconL = R.drawable.ic_custom_account_l,
                iconId = null
            ),
            size = IconSize.L
        )
    }
}

@Preview
@Composable
private fun Preview_Unknown_S() {
    ComponentPreview {
        ItemIcon(
            ItemIcon.Unknown(
                icon = R.drawable.ic_vue_crypto_cardano,
                iconId = null
            ),
            size = IconSize.S
        )
    }
}

@Preview
@Composable
private fun Preview_Unknown_M() {
    ComponentPreview {
        ItemIcon(
            icon = ItemIcon.Unknown(
                icon = R.drawable.ic_vue_crypto_cardano,
                iconId = null
            ),
            size = IconSize.M
        )
    }
}

@Preview
@Composable
private fun Preview_Unknown_L() {
    ComponentPreview {
        ItemIcon(
            icon = ItemIcon.Unknown(
                icon = R.drawable.ic_vue_crypto_cardano,
                iconId = null
            ),
            size = IconSize.L
        )
    }
}
//endregion

