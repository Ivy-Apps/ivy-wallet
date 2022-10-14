package com.ivy.accounts

import androidx.compose.runtime.Immutable
import com.ivy.accounts.data.AccountListItemUi
import com.ivy.design.l2_components.modal.IvyModal

@Immutable
data class AccountTabState(
    val items: List<AccountListItemUi>,
    val createModal: IvyModal,
)