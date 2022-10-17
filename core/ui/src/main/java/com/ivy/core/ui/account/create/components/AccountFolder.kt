package com.ivy.core.ui.account.create.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.account.folder.pick.FolderItem
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.core.ui.data.account.dummyFolderUi
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview

@Composable
internal fun ColumnScope.AccountFolderButton(
    folder: FolderUi?,
    modifier: Modifier = Modifier,
    color: Color,
    onClick: () -> Unit
) {
    B1(
        modifier = Modifier.padding(start = 24.dp),
        text = "Folder"
    )
    SpacerVer(height = 8.dp)
    if (folder != null) {
        FolderItem(
            folder = folder,
            selected = true,
            onClick = onClick,
        )
    } else {
        IvyButton(
            modifier = modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Custom(color),
            text = "Choose folder",
            icon = R.drawable.ic_vue_files_folder,
            onClick = onClick
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview_None() {
    ComponentPreview {
        Column {
            AccountFolderButton(folder = null, color = Purple) {}
        }
    }
}

@Preview
@Composable
private fun Preview_Selected() {
    ComponentPreview {
        Column {
            AccountFolderButton(
                folder = dummyFolderUi("Business"),
                color = Purple,
                onClick = {}
            )
        }
    }
}
// endregion