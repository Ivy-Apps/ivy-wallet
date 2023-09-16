package com.ivy.legacy.data

import androidx.compose.runtime.Immutable
import com.ivy.core.data.model.Account
import com.ivy.core.data.model.Category
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class AppBaseData(
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)
