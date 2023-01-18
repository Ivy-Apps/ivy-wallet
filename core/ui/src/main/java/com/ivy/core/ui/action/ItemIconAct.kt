package com.ivy.core.ui.action

import android.content.Context
import com.ivy.core.domain.action.Action
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.ItemIconId
import com.ivy.resources.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ItemIconAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
) : Action<ItemIconAct.Input, ItemIcon>() {
    data class Input(
        val iconId: ItemIconId?,
        val defaultTo: DefaultTo
    )

    override suspend fun action(input: Input): ItemIcon {
        return itemIcon(
            appContext = appContext,
            iconId = input.iconId,
            defaultTo = input.defaultTo,
        )
    }
}

fun itemIcon(
    appContext: Context,
    iconId: ItemIconId?,
    defaultTo: DefaultTo,
): ItemIcon {
    fun default(): ItemIcon = when (defaultTo) {
        DefaultTo.Folder -> ItemIcon.Unknown(
            icon = R.drawable.ic_vue_files_folder,
            iconId = "ic_vue_files_folder",
        )
        else -> ItemIcon.Sized(
            iconS = when (defaultTo) {
                DefaultTo.Account -> R.drawable.ic_custom_account_s
                DefaultTo.Category -> R.drawable.ic_custom_category_s
                else -> error("not expected size icon")
            },
            iconM = when (defaultTo) {
                DefaultTo.Account -> R.drawable.ic_custom_account_m
                DefaultTo.Category -> R.drawable.ic_custom_category_m
                else -> error("not expected size icon")
            },
            iconL = when (defaultTo) {
                DefaultTo.Account -> R.drawable.ic_custom_account_l
                DefaultTo.Category -> R.drawable.ic_custom_category_l
                else -> error("not expected size icon")
            },
            iconId = iconId
        )
    }

    return iconId?.let { itemIconOptional(appContext, iconId) } ?: default()
}

enum class DefaultTo {
    Account,
    Category,
    Folder
}