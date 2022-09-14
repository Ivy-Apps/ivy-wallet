package com.ivy.core.domain.functions.dummy

import androidx.annotation.DrawableRes
import com.ivy.data.icon.IvyIcon

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