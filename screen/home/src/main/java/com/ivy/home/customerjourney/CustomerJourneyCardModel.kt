package com.ivy.home.customerjourney

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.ivy.design.l0_system.Gradient
import com.ivy.domain.RootScreen
import com.ivy.legacy.IvyWalletCtx
import com.ivy.navigation.Navigation
import com.ivy.wallet.io.persistence.datastore.IvyDataStore

@Immutable
data class CustomerJourneyCardModel(
    val id: String,
    val condition: suspend (trnCount: Long, plannedPaymentsCount: Long, ivyContext: IvyWalletCtx, context: Context) -> Boolean,
    val title: String,
    val description: String,
    val cta: String?,
    @DrawableRes val ctaIcon: Int,

    val hasDismiss: Boolean = true,

    val background: Gradient,
    val onAction: (Navigation, IvyWalletCtx, RootScreen) -> Unit
)
