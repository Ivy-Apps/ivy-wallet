package com.ivy.data.account

import androidx.annotation.ColorInt
import com.ivy.data.CurrencyCode
import com.ivy.data.icon.IvyIcon
import java.util.*

data class Account(
    val id: UUID,

    val name: String,
    val currency: CurrencyCode,

    @ColorInt
    val color: Int,
    val icon: IvyIcon,

    val excluded: Boolean,

    val metadata: AccMetadata,
)