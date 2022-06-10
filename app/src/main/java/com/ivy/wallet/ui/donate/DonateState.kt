package com.ivy.wallet.ui.donate

import com.ivy.wallet.android.billing.Plan

sealed class DonateState {
    object Loading : DonateState()

    data class Success(
        val donate5: Pair<String, Plan>,
        val donate10: Pair<String, Plan>,
        val donate15: Pair<String, Plan>,
        val donate25: Pair<String, Plan>,
        val donate50: Pair<String, Plan>
    )

    data class Error(
        val errMsg: String
    ) : DonateState()
}