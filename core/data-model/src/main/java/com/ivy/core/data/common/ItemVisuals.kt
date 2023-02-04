package com.ivy.core.data.common

import androidx.annotation.ColorInt

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
