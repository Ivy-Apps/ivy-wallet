package com.ivy.attributions

sealed interface AttributionItem {
    data class Attribution(val name: String, val link: String) : AttributionItem
    data class Divider(val sectionName: String) : AttributionItem
}