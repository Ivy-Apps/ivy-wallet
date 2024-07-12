package com.ivy.ui

sealed class ViewModelEvent {
    data object StartLocaleActivity : ViewModelEvent()
}