package com.ivy.core.ui.algorithm.trnhistory.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.core.ui.data.transaction.dummyTrnTimeActualUi
import com.ivy.data.transaction.TransactionType
import java.util.*

@Immutable
data class TransactionUi(
    val id: String,
    val value: ValueUi,
    val time: TrnTimeUi,
    val account: AccountUi,
    val category: CategoryUi?,
    val title: String?,
    val description: String?,
    val type: TransactionType,
) : TrnListItemUi

@Composable
fun dummyTransactionUi(
    type: TransactionType,
    value: ValueUi,
    account: AccountUi = dummyAccountUi(),
    category: CategoryUi? = dummyCategoryUi(),
    title: String? = null,
    description: String? = null,
    time: TrnTimeUi = dummyTrnTimeActualUi(),
) = TransactionUi(
    id = UUID.randomUUID().toString(),
    type = type,
    value = value,
    account = account,
    category = category,
    title = title,
    description = description,
    time = time
)