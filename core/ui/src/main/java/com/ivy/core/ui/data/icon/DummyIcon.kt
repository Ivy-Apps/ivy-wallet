package com.ivy.core.ui.data.icon

import androidx.annotation.DrawableRes

fun dummyIconSized(
    @DrawableRes
    icon: Int
) = ItemIcon.Sized(
    iconS = icon,
    iconM = icon,
    iconL = icon,
    iconId = null
)

fun dummyIconUnknown(
    @DrawableRes
    icon: Int
) = ItemIcon.Unknown(
    icon = icon,
    iconId = null
)