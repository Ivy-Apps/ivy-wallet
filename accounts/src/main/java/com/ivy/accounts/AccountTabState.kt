package com.ivy.accounts

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.AccountUi
import com.ivy.design.l2_components.modal.IvyModal

@Immutable
data class AccountTabState(
    val accounts: List<AccountUi>,
    val createAccountModal: IvyModal,
)