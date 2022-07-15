package com.ivy.old.component.transaction

import androidx.compose.runtime.Composable
import com.ivy.base.ivyWalletCtx
import com.ivy.data.Account
import com.ivy.data.Category
import java.util.*

@Composable
fun category(
    categoryId: UUID?,
    categories: List<Category>
): Category? {
    val targetId = categoryId ?: return null
    return ivyWalletCtx().categoryMap[targetId] ?: categories.find { it.id == targetId }
}

@Composable
fun account(
    accountId: UUID?,
    accounts: List<Account>
): Account? {
    val targetId = accountId ?: return null
    return ivyWalletCtx().accountMap[targetId] ?: accounts.find { it.id == targetId }
}