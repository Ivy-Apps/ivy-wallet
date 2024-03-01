package com.ivy.legacy.data

import androidx.compose.runtime.Immutable
import com.ivy.data.model.Category
import com.ivy.legacy.datamodel.Account
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class AppBaseData(
    val baseCurrency: String,
    val accounts: ImmutableList<Account>,
    val categories: ImmutableList<Category>
)
