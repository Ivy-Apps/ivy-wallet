package com.ivy.core.data

import androidx.annotation.ColorInt

/**
 * Defines the visual aspects of an item.
 * @param name display name of the item like a title
 * @param description optional description providing more information about the item
 * @param iconId [ItemIconId]
 * @param color [IvyColor]
 */
data class ItemVisuals(
    val name: String,
    val description: String?,
    val iconId: ItemIconId,
    val color: IvyColor
)

/**
 * A unique [String] id representing an Ivy icon.
 * Like "car", "ic_vue_building_bank", "awesomeicon3".
 */
@JvmInline
value class ItemIconId(val id: String)

/**
 * A packed int (AARRGGBB) representation of an color using [ColorInt].
 */
@JvmInline
value class IvyColor(@ColorInt val color: Int)
