package com.ivy.wallet.ui.donate.data

import com.ivy.wallet.android.billing.Plan

data class DonateOption(
    val title: String,
    val desc: String,
    val plan: Plan
)