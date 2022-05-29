package com.ivy.wallet.ui.settings.experimental

sealed class ExpEvent {
    object Load : ExpEvent()

    data class SetSmallTrnsPref(val newValue: Boolean) : ExpEvent()

    data class SetNewEditPref(val newValue: Boolean) : ExpEvent()
}