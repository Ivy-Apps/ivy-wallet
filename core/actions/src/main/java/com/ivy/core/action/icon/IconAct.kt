package com.ivy.core.action.icon

import android.content.Context
import androidx.annotation.DrawableRes
import com.ivy.base.R
import com.ivy.data.IvyIconId
import com.ivy.data.icon.IconSize
import com.ivy.data.icon.IvyIcon
import com.ivy.frp.action.FPAction
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IconAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context
) : FPAction<IconAct.Input, IvyIcon>() {
    data class Input(
        val iconId: IvyIconId?,
        val defaultTo: DefaultTo
    )

    override suspend fun Input.compose(): suspend () -> IvyIcon = {
        parseIvyIcon()
    }

    private fun Input.parseIvyIcon(): IvyIcon {
        fun Input.default(): IvyIcon = IvyIcon.Sized(
            iconS = when (defaultTo) {
                DefaultTo.Account -> R.drawable.ic_custom_account_s
                DefaultTo.Category -> R.drawable.ic_custom_category_s
            },
            iconM = when (defaultTo) {
                DefaultTo.Account -> R.drawable.ic_custom_account_m
                DefaultTo.Category -> R.drawable.ic_custom_category_m
            },
            iconL = when (defaultTo) {
                DefaultTo.Account -> R.drawable.ic_custom_account_l
                DefaultTo.Category -> R.drawable.ic_custom_category_l
            },
            iconId = iconId
        )

        fun Input.unknown(): IvyIcon =
            getIcon(iconId = iconId)?.let { iconRes ->
                IvyIcon.Unknown(
                    icon = iconRes,
                    iconId = iconId,
                )
            } ?: default()


        val iconS = getSizedIcon(iconId = iconId, size = IconSize.S) ?: return unknown()
        val iconM = getSizedIcon(iconId = iconId, size = IconSize.M) ?: return unknown()
        val iconL = getSizedIcon(iconId = iconId, size = IconSize.L) ?: return unknown()

        return IvyIcon.Sized(
            iconS = iconS,
            iconM = iconM,
            iconL = iconL,
            iconId = iconId,
        )
    }

    @DrawableRes
    fun getSizedIcon(
        iconId: IvyIconId?,
        size: IconSize,
    ): Int? = iconId?.let {
        getDrawableByName(
            fileName = "ic_custom_${normalize(iconId)}_${size.value}"
        )
    }

    @DrawableRes
    private fun getIcon(
        iconId: IvyIconId?
    ): Int? = iconId?.let {
        getDrawableByName(
            fileName = normalize(iconId)
        )
    }

    @DrawableRes
    private fun getDrawableByName(fileName: String): Int? = try {
        appContext.resources.getIdentifier(
            fileName,
            "drawable",
            appContext.packageName
        ).takeIf { it != 0 }
    } catch (e: Exception) {
        null
    }

    private fun normalize(iconId: IvyIconId): String = iconId
        .replace(" ", "")
        .trim()
        .lowercase()
}

enum class DefaultTo {
    Account,
    Category
}