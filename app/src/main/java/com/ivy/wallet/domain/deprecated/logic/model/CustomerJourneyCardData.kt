package com.ivy.wallet.domain.deprecated.logic.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.ivy.frp.view.navigation.Navigation
import com.ivy.wallet.ui.IvyWalletCtx
import com.ivy.wallet.ui.RootActivity

data class CustomerJourneyCardData(
    val id: String,
    val condition: (trnCount: Long, plannedPaymentsCount: Long, ivyContext: IvyWalletCtx) -> Boolean,

    val title: String,
    val description: String,
    val cta: String,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val backgroundColor: Color,
    val onAction: (Navigation, IvyWalletCtx, RootActivity) -> Unit
)