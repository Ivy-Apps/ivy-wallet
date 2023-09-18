package com.ivy.legacy.ui.component.transaction

import androidx.compose.runtime.Composable
import com.ivy.legacy.ivyWalletCtx
import com.ivy.core.datamodel.Account
import com.ivy.core.datamodel.Category
import java.util.UUID

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
