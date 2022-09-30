package com.ivy.core.ui.action

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.DrawableRes
import com.ivy.core.domain.action.Action
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.ItemIconId
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ItemIconOptionalAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context
) : Action<ItemIconId, ItemIcon?>() {
    override suspend fun ItemIconId.willDo(): ItemIcon? {
        fun ItemIconId.unknown(): ItemIcon? =
            getIcon(iconId = this)?.let { iconRes ->
                ItemIcon.Unknown(
                    icon = iconRes,
                    iconId = this,
                )
            }

        val iconS = getSizedIcon(iconId = this, size = IconSize.S) ?: return unknown()
        val iconM = getSizedIcon(iconId = this, size = IconSize.M) ?: return unknown()
        val iconL = getSizedIcon(iconId = this, size = IconSize.L) ?: return unknown()

        return ItemIcon.Sized(
            iconS = iconS,
            iconM = iconM,
            iconL = iconL,
            iconId = this,
        )
    }

    @DrawableRes
    fun getSizedIcon(
        iconId: ItemIconId?,
        size: IconSize,
    ): Int? = iconId?.let {
        getDrawableByName(
            fileName = "ic_custom_${normalize(iconId)}_${size.value}"
        )
    }

    @DrawableRes
    private fun getIcon(
        iconId: ItemIconId?
    ): Int? = iconId?.let {
        getDrawableByName(
            fileName = normalize(iconId)
        )
    }

    @SuppressLint("DiscouragedApi")
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

    private fun normalize(iconId: ItemIconId): String = iconId
        .replace(" ", "")
        .trim()
        .lowercase()
}