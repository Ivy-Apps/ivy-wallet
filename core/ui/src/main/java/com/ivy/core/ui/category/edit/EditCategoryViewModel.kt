package com.ivy.core.ui.category.edit

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.toUUID
import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.category.CategoriesFlow
import com.ivy.core.domain.action.category.CategoryByIdAct
import com.ivy.core.domain.action.category.WriteCategoriesAct
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.ui.R
import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.ItemIconId
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l0_system.color.toComposeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
internal class EditCategoryViewModel @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val itemIconAct: ItemIconAct,
    private val writeCategoriesAct: WriteCategoriesAct,
    private val categoryById: CategoryByIdAct,
    private val categoriesFlow: CategoriesFlow,
    private val mapCategoryUiAct: MapCategoryUiAct,
    private val timeProvider: TimeProvider,
) : SimpleFlowViewModel<EditCategoryState, EditCategoryEvent>() {
    override val initialUi = EditCategoryState(
        categoryId = "",
        icon = ItemIcon.Sized(
            iconM = R.drawable.ic_custom_category_m,
            iconS = R.drawable.ic_custom_category_s,
            iconL = R.drawable.ic_custom_category_l,
            iconId = null
        ),
        color = Purple,
        initialName = "",
        parent = null,
        archived = false,
        type = CategoryType.Both,
    )

    private val category = MutableStateFlow<Category?>(null)
    private var name = ""
    private val initialName = MutableStateFlow(initialUi.initialName)
    private val iconId = MutableStateFlow<ItemIconId?>(null)
    private val color = MutableStateFlow(initialUi.color)
    private val parentCategoryId = MutableStateFlow<String?>(null)
    private val archived = MutableStateFlow(initialUi.archived)
    private val type = MutableStateFlow(initialUi.type)

    override val uiFlow: Flow<EditCategoryState> = combine(
        category, headerFlow(), secondaryFlow(), parentFlow()
    ) { category, header, secondary, parent ->
        EditCategoryState(
            categoryId = category?.id?.toString() ?: "",
            icon = itemIconAct(ItemIconAct.Input(header.iconId, DefaultTo.Category)),
            initialName = header.initialName,
            color = header.color,
            parent = parent,
            archived = secondary.archived,
            type = secondary.type,
        )
    }

    private fun headerFlow(): Flow<Header> = combine(
        iconId, initialName, color,
    ) { iconId, initialName, color ->
        Header(iconId = iconId, initialName = initialName, color = color)
    }

    private fun secondaryFlow(): Flow<Secondary> = combine(
        type, archived
    ) { type, archived ->
        Secondary(type, archived)
    }

    private fun parentFlow(): Flow<CategoryUi?> = combine(
        categoriesFlow(), parentCategoryId
    ) { categories, parentId ->
        categories.firstOrNull { it.id.toString() == parentId }
            ?.let { mapCategoryUiAct(it) }
    }


    // region Event Handling
    override suspend fun handleEvent(event: EditCategoryEvent) = when (event) {
        is EditCategoryEvent.Initial -> handleInitial(event)
        EditCategoryEvent.EditCategory -> editCategory()
        is EditCategoryEvent.IconChange -> handleIconPick(event)
        is EditCategoryEvent.NameChange -> handleNameChange(event)
        is EditCategoryEvent.ColorChange -> handleColorChange(event)
        is EditCategoryEvent.ParentChange -> handleFolderChange(event)
        is EditCategoryEvent.TypeChange -> handleTypeChange(event)
        EditCategoryEvent.Archive -> handleArchive()
        EditCategoryEvent.Unarchive -> handleUnarchive()
        EditCategoryEvent.Delete -> handleDelete()
    }

    private suspend fun handleInitial(event: EditCategoryEvent.Initial) {
        // we need a snapshot of the category at this given point in time
        // => flow isn't good for that use-case
        categoryById(event.categoryId)?.let {
            category.value = it
            name = it.name
            initialName.value = it.name
            iconId.value = it.icon
            color.value = it.color.toComposeColor()
            parentCategoryId.value = it.parentCategoryId?.toString()
            type.value = it.type
            archived.value = it.state == CategoryState.Archived
        }
    }

    private suspend fun editCategory() {
        val updated = category.value?.copy(
            name = name,
            color = color.value.toArgb(),
            parentCategoryId = parentCategoryId.value?.toUUID(),
            icon = iconId.value,
            type = type.value,
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = timeProvider.timeNow(),
            )
        )
        if (updated != null) {
            writeCategoriesAct(Modify.save(updated))
        }
    }

    private fun handleIconPick(event: EditCategoryEvent.IconChange) {
        iconId.value = event.iconId
    }

    private fun handleNameChange(event: EditCategoryEvent.NameChange) {
        name = event.name
    }

    private fun handleColorChange(event: EditCategoryEvent.ColorChange) {
        color.value = event.color
    }

    private fun handleFolderChange(event: EditCategoryEvent.ParentChange) {
        parentCategoryId.value = event.parent?.id
    }

    private fun handleTypeChange(event: EditCategoryEvent.TypeChange) {
        type.value = event.type
    }

    private suspend fun handleArchive() {
        archived.value = true
        updateArchived(state = CategoryState.Archived)
        showToast("Category archived")
    }

    private suspend fun handleUnarchive() {
        archived.value = false
        updateArchived(state = CategoryState.Default)
        showToast("Category unarchived")
    }

    private fun showToast(text: String) {
        Toast.makeText(appContext, text, Toast.LENGTH_LONG).show()
    }

    private suspend fun updateArchived(state: CategoryState) {
        val updated = category.value?.copy(
            state = state,
            sync = Sync(
                state = SyncState.Syncing,
                lastUpdated = timeProvider.timeNow(),
            )
        )
        if (updated != null) {
            writeCategoriesAct(Modify.save(updated))
        }
    }

    private suspend fun handleDelete() {
        category.value?.let {
            writeCategoriesAct(Modify.delete(it.id.toString()))
        }
    }
    // endregion

    private data class Header(
        val iconId: ItemIconId?,
        val initialName: String,
        val color: Color,
    )

    private data class Secondary(
        val type: CategoryType,
        val archived: Boolean,
    )
}