package com.ivy.core.ui.account.reorder

import ReorderModal
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.account.reorder.data.ReorderAccListItemUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.account.dummyFolderUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.*
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.DividerW
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@Composable
fun BoxScope.ReorderAccountsModal(
    modal: IvyModal,
    level: Int = 1,
) {
    val viewModel: ReorderAccountsViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel, preview = ::previewState)

    ReorderModal(
        modal = modal,
        level = level,
        items = state.items,
        onReorder = {
            viewModel?.onEvent(ReorderAccountsEvent.Reorder(it))
        }
    ) { _, item ->
        Item(item = item)
    }
}

@Composable
private fun RowScope.Item(item: ReorderAccListItemUi) {
    when (item) {
        is ReorderAccListItemUi.AccountHolder -> AccountCard(account = item.account)
        is ReorderAccListItemUi.FolderHolder -> FolderCard(folder = item.folder)
        ReorderAccListItemUi.FolderEnd -> FolderEnd()
    }
}

@Composable
private fun AccountCard(account: AccountUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp) // margin top
            .padding(start = 8.dp, end = 16.dp)
            .background(account.color, UI.shapes.rounded)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contrast = rememberContrast(account.color)
        ItemIcon(itemIcon = account.icon, size = IconSize.S, tint = contrast)
        SpacerHor(width = 4.dp)
        B2(text = account.name, color = contrast)
    }
}

@Composable
private fun FolderCard(folder: FolderUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp) // margin top
            .padding(start = 8.dp, end = 16.dp)
            .background(folder.color, UI.shapes.squared)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contrast = rememberContrast(folder.color)
        ItemIcon(itemIcon = folder.icon, size = IconSize.S, tint = contrast)
        SpacerHor(width = 4.dp)
        B2(text = folder.name, color = contrast)
    }
}

@Composable
private fun RowScope.FolderEnd() {
    SpacerHor(width = 8.dp)
    DividerW()
    SpacerHor(width = 8.dp)
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        ReorderAccountsModal(modal = modal)
    }
}

private fun previewState() = ReorderAccountsStateUi(
    items = listOf(
        dummyAccountHolder("Account 1", color = Green),
        dummyAccountHolder("Account 2", color = Purple),
        dummyFolderHolder("Folder 1", color = Green2Dark),
        dummyAccountHolder("Account 3", color = Red2),
        dummyAccountHolder("Account 4", color = YellowDark),
        dummyFolderHolder("Folder 2", color = Green),
        dummyAccountHolder("Account 5", color = Blue),
        dummyFolderHolder("Folder 3", color = Green),
    ),
)

private fun dummyAccountHolder(name: String, color: Color) = ReorderAccListItemUi.AccountHolder(
    dummyAccountUi(name = name, color = color),
)

private fun dummyFolderHolder(name: String, color: Color) = ReorderAccListItemUi.FolderHolder(
    dummyFolderUi(name = name, color = color)
)
// endregion