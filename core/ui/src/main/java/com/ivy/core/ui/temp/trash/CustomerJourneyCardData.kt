package com.ivy.core.ui.temp.trash

import androidx.annotation.DrawableRes
import com.ivy.design.l0_system.color.Gradient

data class CustomerJourneyCardData(
    val id: String,
    val condition: (trnCount: Long, plannedPaymentsCount: Long, ivyContext: IvyWalletCtx) -> Boolean,

    val title: String,
    val description: String,
    val cta: String,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val background: Gradient,
    val onAction: (IvyWalletCtx, com.ivy.core.ui.temp.RootScreen) -> Unit
)