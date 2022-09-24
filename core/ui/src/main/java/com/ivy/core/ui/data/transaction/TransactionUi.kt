package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.FormattedValue
import com.ivy.core.ui.data.AccountUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyAccountUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.data.transaction.TrnType

@Immutable
data class TransactionUi(
    val id: String,
    val type: TrnType,
    val value: FormattedValue,
    val account: AccountUi,
    val category: CategoryUi?,
    val title: String?,
    val description: String?,
    val time: TrnTimeUi,
)

@Composable
fun dummyTransactionUi(
    type: TrnType,
    value: FormattedValue,
    account: AccountUi = dummyAccountUi(),
    category: CategoryUi? = dummyCategoryUi(),
    title: String? = null,
    description: String? = null,
    time: TrnTimeUi = dummyTrnTimeActualUi(),
) = TransactionUi(
    id = "",
    type = type,
    value = value,
    account = account,
    category = category,
    title = title,
    description = description,
    time = time
)