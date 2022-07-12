package com.ivy.donate

import android.app.Activity
import com.ivy.donate.data.DonateOption

sealed class DonateEvent {
    data class Load(val activity: Activity) : DonateEvent()

    data class Donate(
        val activity: Activity,
        val option: DonateOption
    ) : DonateEvent()
}