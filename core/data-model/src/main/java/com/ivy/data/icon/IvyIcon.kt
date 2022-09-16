package com.ivy.data.icon

import androidx.annotation.DrawableRes
import com.ivy.data.IvyIconId

sealed class IvyIcon {

    data class Sized(
        @DrawableRes
        val iconS: Int,
        @DrawableRes
        val iconM: Int,
        @DrawableRes
        val iconL: Int,
        val iconId: IvyIconId?,
    ) : IvyIcon()

    data class Unknown(
        @DrawableRes
        val icon: Int,
        val iconId: IvyIconId?,
    ) : IvyIcon()
}

