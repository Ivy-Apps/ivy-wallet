package com.ivy.core.ui.icon.picker

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.pure.ui.groupByRows
import com.ivy.core.ui.action.ItemIconOptionalAct
import com.ivy.core.ui.icon.picker.data.Icon
import com.ivy.core.ui.icon.picker.data.SectionUi
import com.ivy.core.ui.icon.picker.data.SectionUnverified
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
internal class IconPickerViewModel @Inject constructor(
    private val itemIconOptionalAct: ItemIconOptionalAct
) : SimpleFlowViewModel<IconPickerStateUi, IconPickerEvent>() {
    companion object {
        const val ICONS_PER_ROW = 4
    }

    override val initialUi = IconPickerStateUi(
        sections = emptyList(),
        searchQuery = ""
    )

    private val searchQuery = MutableStateFlow("")

    override val uiFlow: Flow<IconPickerStateUi> = sectionsUiFlow().map { sections ->
        IconPickerStateUi(
            sections = sections,
            searchQuery = searchQuery.value
        )
    }

    private fun sectionsUiFlow(): Flow<List<SectionUi>> = sectionsFlow().map { sections ->
        sections.mapNotNull { section ->
            // Transform ItemIconId -> ItemIcon and filter empty sections
            val itemIcons = section.icons.mapNotNull {
                itemIconOptionalAct(it.iconId)
            }
            if (itemIcons.isNotEmpty()) {
                SectionUi(
                    name = section.name,
                    iconRows = groupByRows(itemIcons, itemsPerRow = ICONS_PER_ROW),
                )
            } else null
        }
    }

    @OptIn(FlowPreview::class)
    private fun sectionsFlow(): Flow<List<SectionUnverified>> = searchQuery
        .debounce(100)
        .map { query ->
            val sections = pickerItems()
            val normalizedQuery = query.trim().lowercase().takeIf { it.isNotEmpty() }
            if (normalizedQuery != null) {
                listOf(
                    SectionUnverified(
                        name = "Search result",
                        icons = sections.flatMap { section ->
                            section.icons.filter { icon ->
                                passesSearch(icon = icon, query = normalizedQuery)
                            }
                        }
                    )
                )
            } else sections
        }

    private fun passesSearch(icon: Icon, query: String): Boolean =
        // Icon must have at least one keyword that contains the search query
        icon.keywords.any { keyword -> keyword.contains(query) }


    // region Event Handling
    override suspend fun handleEvent(event: IconPickerEvent) = when (event) {
        is IconPickerEvent.Search -> handleSearchQuery(event)
    }

    private fun handleSearchQuery(event: IconPickerEvent.Search) {
        searchQuery.value = event.query.lowercase()
    }
    // endregion
}