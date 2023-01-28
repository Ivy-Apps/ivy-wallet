package com.ivy.core.data.common

/**
 * Indicates that the item can be reordered by the user.
 * For the reordering to happen efficiently the item must have an [orderNum].
 */
interface Reorderable {
    val orderNum: Double
}