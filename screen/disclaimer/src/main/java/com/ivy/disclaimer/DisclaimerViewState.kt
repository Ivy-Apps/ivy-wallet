package com.ivy.disclaimer

import kotlinx.collections.immutable.ImmutableList

data class DisclaimerViewState(
    val checkboxes: ImmutableList<CheckboxViewState>,
    val agreeButtonEnabled: Boolean,
)

data class CheckboxViewState(
    val text: String,
    val checked: Boolean
)

sealed interface DisclaimerViewEvent {
    data class OnCheckboxClick(val index: Int) : DisclaimerViewEvent
    data object OnAgreeClick : DisclaimerViewEvent
}