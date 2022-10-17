package com.ivy.core.ui.account.folder.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.account.folder.create.CreateAccFolderModal
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.core.ui.data.account.dummyFolderUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.*
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Negative
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.design.util.thenWhen

@Composable
fun BoxScope.FolderPickerModal(
    modal: IvyModal,
    selected: FolderUi?,
    level: Int = 1,
    onPickFolder: (FolderUi?) -> Unit,
) {
    val viewModel: FolderPickerViewModel? = hiltViewModelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value
        ?: previewState()

    val createFolderModal = rememberIvyModal()

    Modal(
        modal = modal,
        level = level,
        actions = {
            Negative(text = "No folder") {
                onPickFolder(null)
                modal.hide()
            }
        }
    ) {
        LazyColumn {
            item {
                Title(text = "Choose folder")
            }
            folderItems(
                folders = state.folders,
                selected = selected,
                onSelect = {
                    onPickFolder(it)
                    modal.hide()
                }
            )
            createFolderItem(
                onCreateFolder = { createFolderModal.show() }
            )
            item {
                SpacerVer(height = 48.dp) // last item spacer
            }
        }
    }

    CreateAccFolderModal(
        modal = createFolderModal,
        level = level + 1,
    )
}

// region Folders
private fun LazyListScope.folderItems(
    folders: List<FolderUi>,
    selected: FolderUi?,
    onSelect: (FolderUi) -> Unit
) {
    items(
        items = folders,
        key = { "folder_${it.id}" }
    ) { folder ->
        SpacerVer(height = 12.dp)
        FolderItem(
            folder = folder,
            selected = folder.id == selected?.id
        ) {
            onSelect(folder)
        }
    }
}

@Composable
internal fun FolderItem(
    folder: FolderUi,
    selected: Boolean,
    onClick: () -> Unit
) {
    val dynamicContrast = rememberDynamicContrast(folder.color)
    val contrastColor = rememberContrast(folder.color)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.squared)
            .thenWhen {
                when (selected) {
                    true -> background(folder.color, UI.shapes.squared)
                        .border(2.dp, dynamicContrast, UI.shapes.squared)
                    false -> border(2.dp, dynamicContrast, UI.shapes.squared)
                }
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val color = if (selected) contrastColor else UI.colorsInverted.pure
        ItemIcon(
            itemIcon = folder.icon,
            size = IconSize.S,
            tint = color,
        )
        SpacerHor(width = 8.dp)
        B2(text = folder.name, color = color)
    }
}
// endregion

// region Add folder
fun LazyListScope.createFolderItem(
    onCreateFolder: () -> Unit,
) {
    item(key = "add_folder_item") {
        SpacerVer(height = 12.dp)
        IvyButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Positive,
            text = "New folder",
            icon = R.drawable.ic_round_add_24,
            onClick = onCreateFolder,
        )
    }
}
// endregion

// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        FolderPickerModal(
            modal = modal,
            selected = dummyFolderUi(id = "folder1"),
            onPickFolder = {}
        )
    }
}

private fun previewState() = FolderPickerState(
    folders = listOf(
        dummyFolderUi(id = "folder1", name = "Folder 1", color = Green),
        dummyFolderUi("Folder 2", color = Yellow),
        dummyFolderUi("Folder 3", color = Purple),
    )
)
// endregion