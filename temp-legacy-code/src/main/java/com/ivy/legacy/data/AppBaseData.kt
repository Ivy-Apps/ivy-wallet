package com.ivy.legacy.data

import androidx.compose.runtime.Immutable
import com.ivy.domain.datamodel.Account
import com.ivy.domain.datamodel.Category
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class AppBaseData(
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)
