package com.ivy.wallet.ui.donate

import com.ivy.wallet.android.billing.Plan
import com.ivy.wallet.ui.RootActivity

sealed class DonateEvent {
    data class Load(val activity: RootActivity) : DonateEvent()

    data class Buy(val plan: Plan) : DonateEvent()
}