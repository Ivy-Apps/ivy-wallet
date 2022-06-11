package com.ivy.wallet.ui.donate

import com.ivy.wallet.ui.RootActivity
import com.ivy.wallet.ui.donate.data.DonateOption

sealed class DonateEvent {
    data class Load(val activity: RootActivity) : DonateEvent()

    data class Donate(
        val activity: RootActivity,
        val option: DonateOption
    ) : DonateEvent()
}