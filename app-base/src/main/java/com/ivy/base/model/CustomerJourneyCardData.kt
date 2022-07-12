package com.ivy.wallet.domain.deprecated.logic.model

import androidx.annotation.DrawableRes
import com.ivy.base.IvyWalletCtx
import com.ivy.base.RootScreen
import com.ivy.design.l0_system.Gradient
import com.ivy.frp.view.navigation.Navigation

data class CustomerJourneyCardData(
    val id: String,
    val condition: (trnCount: Long, plannedPaymentsCount: Long, ivyContext: IvyWalletCtx) -> Boolean,

    val title: String,
    val description: String,
    val cta: String,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val background: Gradient,
    val onAction: (Navigation, IvyWalletCtx, RootScreen) -> Unit
)