package com.ivy.core.ui.data.icon

import androidx.annotation.DrawableRes

fun dummyIconSized(
    @DrawableRes
    icon: Int
) = IvyIcon.Sized(
    iconS = icon,
    iconM = icon,
    iconL = icon,
    iconId = null
)

fun dummyIconUnknown(
    @DrawableRes
    icon: Int
) = IvyIcon.Unknown(
    icon = icon,
    iconId = null
)