package com.ivy.journey.domain

import androidx.annotation.DrawableRes
import com.ivy.design.l0_system.Gradient
import com.ivy.frp.view.navigation.Navigation

data class CustomerJourneyCardData(
    val id: String,
    val condition: (trnCount: Long, plannedPaymentsCount: Long, ivyContext: com.ivy.core.ui.temp.IvyWalletCtx) -> Boolean,

    val title: String,
    val description: String,
    val cta: String,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val background: Gradient,
    val onAction: (Navigation, com.ivy.core.ui.temp.IvyWalletCtx, com.ivy.core.ui.temp.RootScreen) -> Unit
)