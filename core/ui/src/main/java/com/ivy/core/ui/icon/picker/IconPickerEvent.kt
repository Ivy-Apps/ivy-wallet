package com.ivy.core.ui.icon.picker

internal sealed interface IconPickerEvent {
    data class Search(val query: String) : IconPickerEvent
}