package com.ivy.accounts

import androidx.compose.runtime.Immutable
import com.ivy.accounts.data.AccListItemUi
import com.ivy.design.l2_components.modal.IvyModal

@Immutable
data class AccountTabState(
    val items: List<AccListItemUi>,
    val createModal: IvyModal,
)