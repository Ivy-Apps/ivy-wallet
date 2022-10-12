package com.ivy.accounts.components

import androidx.compose.foundation.lazy.LazyListScope
import com.ivy.core.ui.data.account.AccountFolderUi
import com.ivy.core.ui.data.account.AccountListItemUi
import com.ivy.core.ui.data.account.AccountUi

fun LazyListScope.accountsList(
    items: List<AccountListItemUi>,
    onFolderClick: (AccountFolderUi) -> Unit,
    onAccountClick: (AccountUi) -> Unit
) {

}