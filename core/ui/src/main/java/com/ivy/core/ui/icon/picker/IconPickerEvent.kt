package com.ivy.core.ui.icon.picker

sealed interface IconPickerEvent {
    data class Search(val query: String) : IconPickerEvent
}