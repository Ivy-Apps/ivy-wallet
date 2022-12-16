package com.ivy.transaction.create.trn

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi

@Immutable
data class NewTrnState(
    val amount: ValueUi,
    val account: AccountUi,
    val category: CategoryUi,
)