package com.ivy.core.ui.account.folder.choose

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
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe
import com.ivy.design.util.thenWhen

@Composable
fun BoxScope.ChooseFolderModal(
    modal: IvyModal,
    selected: FolderUi?,
    level: Int = 1,
    onChooseFolder: (FolderUi?) -> Unit,
) {
    val viewModel: ChooseFolderViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value
        ?: previewState()

    Modal(
        modal = modal,
        level = level,
        actions = {
            Negative(text = "No folder") {
                onChooseFolder(null)
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
                    onChooseFolder(it)
                    modal.hide()
                }
            )
            item {
                SpacerVer(height = 48.dp) // last item spacer
            }
        }
    }
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


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        ChooseFolderModal(
            modal = modal,
            selected = dummyFolderUi(id = "folder1"),
            onChooseFolder = {}
        )
    }
}

private fun previewState() = ChooseFolderState(
    folders = listOf(
        dummyFolderUi(id = "folder1", name = "Folder 1", color = Green),
        dummyFolderUi("Folder 2", color = Yellow),
        dummyFolderUi("Folder 3", color = Purple),
    )
)
// endregion