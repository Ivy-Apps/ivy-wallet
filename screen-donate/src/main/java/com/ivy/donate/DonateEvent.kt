package com.ivy.donate

import androidx.appcompat.app.AppCompatActivity
import com.ivy.donate.data.DonateOption

sealed class DonateEvent {
    data class Load(val activity: AppCompatActivity) : DonateEvent()

    data class Donate(
        val activity: AppCompatActivity,
        val option: DonateOption
    ) : DonateEvent()
}
