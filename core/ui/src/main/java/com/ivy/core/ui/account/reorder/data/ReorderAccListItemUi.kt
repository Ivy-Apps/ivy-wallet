package com.ivy.core.ui.account.reorder.data

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.FolderUi

@Immutable
sealed interface ReorderAccListItemUi {
    @Immutable
    data class AccountHolder(val account: AccountUi) : ReorderAccListItemUi

    @Immutable
    data class FolderHolder(val folder: FolderUi) : ReorderAccListItemUi

    @Immutable
    object FolderEnd : ReorderAccListItemUi
}