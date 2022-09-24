package com.ivy.core.ui.data.icon

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.ivy.data.IvyIconId

@Immutable
sealed interface IvyIcon {
    @Immutable
    data class Sized(
        @DrawableRes
        val iconS: Int,
        @DrawableRes
        val iconM: Int,
        @DrawableRes
        val iconL: Int,
        val iconId: IvyIconId?,
    ) : IvyIcon

    @Immutable
    data class Unknown(
        @DrawableRes
        val icon: Int,
        val iconId: IvyIconId?,
    ) : IvyIcon
}

