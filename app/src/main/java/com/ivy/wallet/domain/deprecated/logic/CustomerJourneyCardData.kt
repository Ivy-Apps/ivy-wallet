package com.ivy.wallet.domain.deprecated.logic

import androidx.annotation.DrawableRes
import com.ivy.core.ui.IvyWalletCtx
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.ui.RootActivity

data class CustomerJourneyCardData(
    val id: String,
    val condition: (trnCount: Long, plannedPaymentsCount: Long, ivyContext: IvyWalletCtx) -> Boolean,

    val title: String,
    val description: String,
    val cta: String?,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val background: com.ivy.design.l0_system.Gradient,
    val onAction: (Navigation, IvyWalletCtx, RootActivity) -> Unit
)
