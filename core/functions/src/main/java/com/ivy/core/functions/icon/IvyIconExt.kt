package com.ivy.core.functions.icon

import com.ivy.data.IvyIconId
import com.ivy.data.icon.IvyIcon

fun IvyIcon.iconId(): IvyIconId? = when (this) {
    is IvyIcon.Sized -> iconId
    is IvyIcon.Unknown -> iconId
}