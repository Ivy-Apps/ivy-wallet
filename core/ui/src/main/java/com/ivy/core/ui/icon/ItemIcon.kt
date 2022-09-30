package com.ivy.core.ui.icon

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ivy.core.ui.R
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.isInPreview

@Composable
fun ItemIcon(
    itemIcon: ItemIcon,
    size: IconSize,
    modifier: Modifier = Modifier,
    tint: Color = UI.colorsInverted.pure
) {
    val sizeModifier = remember(modifier, size) {
        modifier.size(size.toDp())
    }
    when (itemIcon) {
        is ItemIcon.Sized -> AsyncIcon(
            modifier = sizeModifier,
            icon = itemIcon.icon(size),
            tint = tint,
        )
        is ItemIcon.Unknown -> Image(
            modifier = sizeModifier
                .padding(all = 4.dp),
            painter = previewSafeAsyncPainter(
                icon = itemIcon.icon,
                contentScale = ContentScale.FillBounds
            ),
            contentDescription = "itemIcon",
            colorFilter = ColorFilter.tint(tint),
            alignment = Alignment.Center,
            contentScale = ContentScale.FillBounds,
        )
    }
}

@DrawableRes
fun ItemIcon.Sized.icon(size: IconSize): Int = when (size) {
    IconSize.S -> iconS
    IconSize.M -> iconM
    IconSize.L -> iconL
}

fun IconSize.toDp(): Dp = when (this) {
    IconSize.S -> 32.dp
    IconSize.M -> 48.dp
    IconSize.L -> 64.dp
}

// region AsyncIcon
@Composable
private fun AsyncIcon(
    @DrawableRes
    icon: Int,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier,
        painter = previewSafeAsyncPainter(icon),
        tint = tint,
        contentDescription = null,
    )
}

@Composable
private fun previewSafeAsyncPainter(
    @DrawableRes
    icon: Int,
    contentScale: ContentScale = ContentScale.Fit,
) = if (isInPreview()) painterResource(icon) else rememberAsyncImagePainter(
    model = icon,
    contentScale = contentScale,
    filterQuality = FilterQuality.None,
)
// endregion


//region Previews
@Preview
@Composable
private fun Preview_Sized_S() {
    ComponentPreview {
        ItemIcon(
            itemIcon = ItemIcon.Sized(
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
            itemIcon = ItemIcon.Sized(
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
            itemIcon = ItemIcon.Unknown(
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
            itemIcon = ItemIcon.Unknown(
                icon = R.drawable.ic_vue_crypto_cardano,
                iconId = null
            ),
            size = IconSize.L
        )
    }
}
//endregion

