package com.ivy.core.ui.account.reorder

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@Composable
fun BoxScope.ReorderAccountsModal(
    modal: IvyModal
) {
    val viewModel: ReorderAccountsViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel, preview = ::previewState)
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

private fun previewState() = ReorderAccountsState(
    items = emptyList(),
)
// endregion