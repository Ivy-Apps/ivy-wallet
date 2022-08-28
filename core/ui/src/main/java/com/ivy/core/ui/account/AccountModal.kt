package com.ivy.core.ui.account

import com.ivy.core.ui.modal.IvyModal
import com.ivy.data.account.Account

data class AccountModal(
    val account: Account,
    val modal: IvyModal,
)