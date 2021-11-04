package com.ivy.wallet.ui.theme.components

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.wallet.base.toLowerCaseLocal
import com.ivy.wallet.ui.theme.IvyComponentPreview
import com.ivy.wallet.ui.theme.IvyTheme

@Composable
fun ItemIconL(
    modifier: Modifier = Modifier,
    iconName: String?,
    tint: Color = IvyTheme.colors.pureInverse,
    Default: (@Composable () -> Unit)? = null
) {
    ItemIcon(
        modifier = modifier,
        size = "l",
        iconName = iconName,
        tint = tint,
        Default = Default
    )
}

@Composable
fun ItemIconMDefaultIcon(
    modifier: Modifier = Modifier,
    iconName: String?,
    tint: Color = IvyTheme.colors.pureInverse,
    @DrawableRes defaultIcon: Int
) {
    ItemIconM(
        modifier = modifier,
        iconName = iconName,
        tint = tint,
        Default = {
            Image(
                modifier = modifier,
                painter = painterResource(id = defaultIcon),
                colorFilter = ColorFilter.tint(tint),
                contentDescription = "item icon"
            )
        }
    )
}

@Composable
fun ItemIconM(
    modifier: Modifier = Modifier,
    iconName: String?,
    tint: Color = IvyTheme.colors.pureInverse,
    Default: (@Composable () -> Unit)? = null
) {
    ItemIcon(
        modifier = modifier,
        size = "m",
        iconName = iconName,
        tint = tint,
        Default = Default
    )
}

@Composable
fun ItemIconSDefaultIcon(
    modifier: Modifier = Modifier,
    iconName: String?,
    tint: Color = IvyTheme.colors.pureInverse,
    @DrawableRes defaultIcon: Int
) {
    ItemIconS(
        modifier = modifier,
        iconName = iconName,
        tint = tint,
        Default = {
            Image(
                modifier = modifier,
                painter = painterResource(id = defaultIcon),
                colorFilter = ColorFilter.tint(tint),
                contentDescription = "item icon"
            )
        }
    )
}

@Composable
fun ItemIconS(
    modifier: Modifier = Modifier,
    iconName: String?,
    tint: Color = IvyTheme.colors.pureInverse,
    Default: (@Composable () -> Unit)? = null
) {
    ItemIcon(
        modifier = modifier,
        size = "s",
        iconName = iconName,
        tint = tint,
        Default = Default
    )
}

@Composable
private fun ItemIcon(
    modifier: Modifier = Modifier,
    iconName: String?,
    size: String,
    tint: Color = IvyTheme.colors.pureInverse,
    Default: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    val iconId = getCustomIconId(
        context = context,
        iconName = iconName,
        size = size
    )

    if (iconId != null) {
        Image(
            modifier = modifier,
            painter = painterResource(id = iconId),
            colorFilter = ColorFilter.tint(tint),
            contentDescription = "item icon"
        )
    } else {
        Default?.invoke()
    }
}

@DrawableRes
@Composable
fun getCustomIconIdS(
    iconName: String?,
    @DrawableRes defaultIcon: Int
): Int {
    val context = LocalContext.current
    return getCustomIconId(
        context = context,
        iconName = iconName,
        size = "s"
    ) ?: defaultIcon
}

@DrawableRes
@Composable
fun getCustomIconIdM(
    iconName: String?,
    @DrawableRes defaultIcon: Int
): Int {
    val context = LocalContext.current
    return getCustomIconId(
        context = context,
        iconName = iconName,
        size = "m"
    ) ?: defaultIcon
}

@DrawableRes
@Composable
fun getCustomIconIdL(
    iconName: String?,
    @DrawableRes defaultIcon: Int
): Int {
    val context = LocalContext.current
    return getCustomIconId(
        context = context,
        iconName = iconName,
        size = "l"
    ) ?: defaultIcon
}

@DrawableRes
fun getCustomIconId(
    context: Context,
    iconName: String?,
    size: String,
): Int? {
    return iconName?.let {
        try {
            val iconNameNormalized = iconName
                .replace(" ", "")
                .trim()
                .toLowerCaseLocal()
            context.resources.getIdentifier(
                "ic_custom_${iconNameNormalized}_${size}",
                "drawable",
                context.packageName
            ).takeIf { it != 0 }
        } catch (e: Exception) {
            null
        }
    }
}

@Preview
@Composable
private fun Preview_L() {
    IvyComponentPreview {
        ItemIconL(iconName = "dna")
    }
}

@Preview
@Composable
private fun Preview_M() {
    IvyComponentPreview {
        ItemIconM(iconName = "document")
    }
}

@Preview
@Composable
private fun Preview_S() {
    IvyComponentPreview {
        ItemIconS(iconName = "fooddrink")
    }
}