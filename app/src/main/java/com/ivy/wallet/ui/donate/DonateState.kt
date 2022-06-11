package com.ivy.wallet.ui.donate

sealed class DonateState {
    object Loading : DonateState()

    object Success : DonateState()

    data class Error(
        val errMsg: String
    ) : DonateState()
}