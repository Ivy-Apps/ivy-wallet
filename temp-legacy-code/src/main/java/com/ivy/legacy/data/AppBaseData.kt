package com.ivy.legacy.data

import androidx.compose.runtime.Immutable
import com.ivy.core.datamodel.Account
import com.ivy.core.datamodel.Category
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class AppBaseData(
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)
