package com.ivy.home.customerjourney

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.ivy.legacy.IvyWalletCtx
import com.ivy.core.RootScreen
import com.ivy.design.l0_system.Gradient
import com.ivy.navigation.Navigation
import com.ivy.navigation.navigation

@Immutable
data class CustomerJourneyCardModel(
    val id: String,
    val condition: (trnCount: Long, plannedPaymentsCount: Long, ivyContext: IvyWalletCtx) -> Boolean,

    val title: String,
    val description: String,
    val cta: String?,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val background: Gradient,
    val onAction: (Navigation, IvyWalletCtx, RootScreen) -> Unit
)
