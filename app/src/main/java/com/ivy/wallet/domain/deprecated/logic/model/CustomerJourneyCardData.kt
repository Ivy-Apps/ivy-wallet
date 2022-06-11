package com.ivy.wallet.domain.deprecated.logic.model

import androidx.annotation.DrawableRes
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.RootActivity
import com.ivy.wallet.ui.theme.Gradient

data class CustomerJourneyCardData(
    val id: String,
    val condition: (trnCount: Long, plannedPaymentsCount: Long, ivyContext: IvyWalletCtx) -> Boolean,

    val title: String,
    val description: String,
    val cta: String,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val background: Gradient,
    val onAction: (Navigation, IvyWalletCtx, RootActivity) -> Unit
)