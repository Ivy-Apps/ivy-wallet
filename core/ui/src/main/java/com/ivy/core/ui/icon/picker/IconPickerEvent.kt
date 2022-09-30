package com.ivy.core.ui.icon.picker

sealed interface IconPickerEvent {
    data class SearchQuery(val query: String) : IconPickerEvent
}