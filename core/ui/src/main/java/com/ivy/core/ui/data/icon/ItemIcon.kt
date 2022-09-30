package com.ivy.core.ui.data.icon

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.ivy.data.ItemIconId

@Immutable
sealed interface ItemIcon {
    @Immutable
    data class Sized(
        @DrawableRes
        val iconS: Int,
        @DrawableRes
        val iconM: Int,
        @DrawableRes
        val iconL: Int,
        val iconId: ItemIconId?,
    ) : ItemIcon

    @Immutable
    data class Unknown(
        @DrawableRes
        val icon: Int,
        val iconId: ItemIconId?,
    ) : ItemIcon
}

fun ItemIcon.iconId(): ItemIconId? = when (this) {
    is ItemIcon.Sized -> iconId
    is ItemIcon.Unknown -> iconId
}
