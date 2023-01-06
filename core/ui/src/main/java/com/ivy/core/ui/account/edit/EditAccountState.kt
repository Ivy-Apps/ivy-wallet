package com.ivy.core.ui.account.edit

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.CurrencyCode
import com.ivy.data.Value

@Immutable
internal data class EditAccountState(
    val accountId: String,
    val currency: CurrencyCode,
    val icon: ItemIcon,
    val color: Color,
    val initialName: String,
    val folder: FolderUi?,
    val excluded: Boolean,
    val archived: Boolean,
    val balance: Value,
    val balanceUi: ValueUi,
)