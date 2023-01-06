package com.ivy.accounts.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.accounts.R
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview

@Composable
internal fun BoxScope.CreateModal(
    modal: IvyModal,
    onCreateAccount: () -> Unit,
    onCreateFolder: () -> Unit,
) {
    Modal(modal = modal, actions = {}) {
        Title(text = stringResource(R.string.create))
        SpacerVer(height = 24.dp)
        FolderButton {
            modal.hide()
            onCreateFolder()
        }
        SpacerVer(height = 12.dp)
        AccountButton {
            modal.hide()
            onCreateAccount()
        }
        SpacerVer(height = 24.dp)
    }
}

@Composable
private fun FolderButton(onClick: () -> Unit) {
    IvyButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        size = ButtonSize.Big,
        visibility = Visibility.Medium,
        feeling = Feeling.Positive,
        text = "New folder",
        icon = R.drawable.ic_vue_files_folder,
        onClick = onClick,
    )
}

@Composable
private fun AccountButton(onClick: () -> Unit) {
    IvyButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        size = ButtonSize.Big,
        visibility = Visibility.High,
        feeling = Feeling.Positive,
        text = "New account",
        icon = R.drawable.ic_custom_account_s,
        onClick = onClick,
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        CreateModal(
            modal = modal,
            onCreateAccount = {},
            onCreateFolder = {}
        )
    }
}
// endregion