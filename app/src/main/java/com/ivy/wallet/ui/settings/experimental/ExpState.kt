package com.ivy.wallet.ui.settings.experimental

sealed class ExpState {
    object Initial : ExpState()

    data class Loaded(
        val smallTrnsPref: Boolean,
        val newEditScreen: Boolean
    ) : ExpState()
}