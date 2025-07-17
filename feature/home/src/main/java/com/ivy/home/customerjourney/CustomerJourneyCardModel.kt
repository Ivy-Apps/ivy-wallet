package com.ivy.home.customerjourney

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.ivy.base.time.TimeProvider
import com.ivy.design.l0_system.Gradient
import com.ivy.domain.RootScreen
import com.ivy.legacy.IvyWalletCtx
import com.ivy.navigation.Navigation
import com.ivy.poll.data.PollRepository

@Immutable
data class CustomerJourneyCardModel(
    val id: String,
    @Suppress("MaximumLineLength", "ParameterWrapping", "MaxLineLength", "ParameterListWrapping")
    val condition: suspend (trnCount: Long, plannedPaymentsCount: Long, ivyContext: IvyWalletCtx, deps: CustomerJourneyDeps) -> Boolean,
    val title: String,
    val description: String,
    val cta: String?,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val background: Gradient,
    val onAction: (Navigation, IvyWalletCtx, RootScreen) -> Unit
)

@Immutable
data class CustomerJourneyDeps(
    val pollRepository: PollRepository,
    val timeProvider: TimeProvider,
)
