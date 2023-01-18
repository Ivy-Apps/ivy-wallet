package com.ivy.accounts

import androidx.compose.runtime.Immutable
import com.ivy.accounts.data.AccountListItemUi
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.design.l2_components.modal.IvyModal

@Immutable
data class AccountsState(
    val totalBalance: ValueUi,
    val availableBalance: ValueUi,
    val excludedBalance: ValueUi,
    val noAccounts: Boolean,
    val items: List<AccountListItemUi>,
    val createModal: IvyModal,
    val bottomBarVisible: Boolean,
)