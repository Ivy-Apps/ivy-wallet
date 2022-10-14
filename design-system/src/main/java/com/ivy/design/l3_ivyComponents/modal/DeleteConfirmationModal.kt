package com.ivy.design.l3_ivyComponents.modal

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Body
import com.ivy.design.l2_components.modal.components.Negative
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.DeleteConfirmationModal(
    modal: IvyModal,
    level: Int = 1,
    message: String = "Are you sure that you want to delete it forever? " +
            "Once deleted, it can NOT be undone.",
    onDelete: () -> Unit,
) {
    Modal(
        modal = modal,
        level = level,
        actions = {
            Negative(
                text = "Delete",
                icon = R.drawable.ic_round_delete_forever_24,
                onClick = {
                    modal.hide()
                    onDelete()
                }
            )
        }
    ) {
        Title(
            text = stringResource(R.string.confirm_deletion),
            color = UI.colors.red
        )
        SpacerVer(height = 24.dp)
        Body(text = message)
        SpacerVer(height = 48.dp)
    }
}

// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = IvyModal()
        modal.show()
        DeleteConfirmationModal(modal = modal) {}
    }
}
// endregion