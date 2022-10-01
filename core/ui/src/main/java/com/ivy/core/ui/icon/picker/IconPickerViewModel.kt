package com.ivy.core.ui.icon.picker

import com.ivy.core.domain.FlowViewModel
import com.ivy.core.ui.action.ItemIconOptionalAct
import com.ivy.core.ui.data.icon.ItemIcon
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
) : FlowViewModel<IconPickerStateUi, IconPickerStateUi, IconPickerEvent>() {
    companion object {
        const val ICONS_PER_ROW = 4
    }

    override fun initialState(): IconPickerStateUi = IconPickerStateUi(
        sections = emptyList(),
        searchQuery = ""
    )
    override fun initialUiState(): IconPickerStateUi = initialState()


    private val searchQuery = MutableStateFlow("")

    override fun stateFlow(): Flow<IconPickerStateUi> = sectionsUiFlow().map { sections ->
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
                    iconRows = groupIconsByRows(itemIcons),
                )
            } else null
        }
    }

    private fun groupIconsByRows(icons: List<ItemIcon>): List<List<ItemIcon>> {
        val rows = mutableListOf<List<ItemIcon>>()
        var row = mutableListOf<ItemIcon>()
        for (icon in icons) {
            if (row.size < ICONS_PER_ROW) {
                // row not finished
                row.add(icon)
            } else {
                // row is finished, add it and start the next row
                rows.add(row)
                // row.clear() won't work because it clears the already added row
                row = mutableListOf()
            }
        }
        if (row.isNotEmpty()) {
            // add the last not finished row
            rows.add(row)
        }
        return rows
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

    override suspend fun mapToUiState(state: IconPickerStateUi): IconPickerStateUi = state

    // region Event Handling
    override suspend fun handleEvent(event: IconPickerEvent) = when (event) {
        is IconPickerEvent.Search -> handleSearchQuery(event)
    }

    private fun handleSearchQuery(event: IconPickerEvent.Search) {
        searchQuery.value = event.query.lowercase()
    }
    // endregion
}