package com.ivy.home.customerjourney

import androidx.annotation.DrawableRes
import com.ivy.core.IvyWalletCtx
import com.ivy.core.RootScreen
import com.ivy.frp.view.navigation.Navigation

data class CustomerJourneyCardModel(
    val id: String,
    val condition: (trnCount: Long, plannedPaymentsCount: Long, ivyContext: IvyWalletCtx) -> Boolean,

    val title: String,
    val description: String,
    val cta: String?,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val background: com.ivy.design.l0_system.Gradient,
    val onAction: (Navigation, IvyWalletCtx, RootScreen) -> Unit
)
