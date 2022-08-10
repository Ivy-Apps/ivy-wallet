package com.ivy.data.account

import androidx.annotation.ColorInt
import com.ivy.data.IvyIcon
import java.util.*

data class Account(
    val name: String,
    val currencyCode: String,

    @ColorInt
    val color: Int,
    val icon: IvyIcon?,

    val excluded: Boolean,

    val metadata: AccMetadata,

    val id: UUID
)