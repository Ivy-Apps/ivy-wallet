package com.ivy.wallet.logic.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.ivy.design.navigation.Navigation
import com.ivy.wallet.ui.IvyActivity
import com.ivy.wallet.ui.IvyWalletCtx

data class CustomerJourneyCardData(
    val id: String,
    val condition: (trnCount: Long, plannedPaymentsCount: Long, ivyContext: IvyWalletCtx) -> Boolean,

    val title: String,
    val description: String,
    val cta: String,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val backgroundColor: Color,
    val onAction: (Navigation, IvyWalletCtx, IvyActivity) -> Unit
)