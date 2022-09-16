package com.ivy.core.domain.pure.util

import com.ivy.data.IvyIconId
import com.ivy.data.icon.IvyIcon

fun IvyIcon.iconId(): IvyIconId? = when (this) {
    is IvyIcon.Sized -> iconId
    is IvyIcon.Unknown -> iconId
}