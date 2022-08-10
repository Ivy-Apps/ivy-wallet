package com.ivy.old.component.transaction

import androidx.compose.runtime.Composable
import com.ivy.base.ivyWalletCtx
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import java.util.*

@Composable
fun category(
    categoryId: UUID?,
    categories: List<CategoryOld>
): CategoryOld? {
    val targetId = categoryId ?: return null
    return ivyWalletCtx().categoryMap[targetId] ?: categories.find { it.id == targetId }
}

@Composable
fun account(
    accountId: UUID?,
    accounts: List<AccountOld>
): AccountOld? {
    val targetId = accountId ?: return null
    return ivyWalletCtx().accountMap[targetId] ?: accounts.find { it.id == targetId }
}