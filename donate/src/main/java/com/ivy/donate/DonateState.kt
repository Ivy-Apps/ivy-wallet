package com.ivy.donate

sealed class DonateState {
    object Success : DonateState()

    data class Error(
        val errMsg: String
    ) : DonateState()
}