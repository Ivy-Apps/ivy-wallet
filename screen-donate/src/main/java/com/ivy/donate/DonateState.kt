package com.ivy.donate

sealed class DonateState {
    data object Success : DonateState()

    data class Error(
        val errMsg: String
    ) : DonateState()
}
