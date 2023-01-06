package com.ivy.core.ui.category.edit.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Body
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.DeleteCategoryModal(
    modal: IvyModal,
    level: Int = 1,
    archived: Boolean,
    categoryName: String,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
) {
    Modal(
        modal = modal,
        level = level,
        actions = {
            IvyButton(
                size = ButtonSize.Small,
                visibility = Visibility.Focused,
                feeling = Feeling.Negative,
                text = "Delete forever",
                icon = R.drawable.ic_round_delete_forever_24
            ) {
                modal.hide()
                onDelete()
            }
        }
    ) {
        Title(
            text = "Delete \"$categoryName\" category forever?",
            color = UI.colors.red
        )
        SpacerVer(height = 24.dp)
        Body(
            text = bodyText(
                categoryname = categoryName,
                archived = archived
            )
        )
        if (!archived) {
            SpacerVer(height = 12.dp)
            IvyButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                size = ButtonSize.Big,
                visibility = Visibility.Medium,
                feeling = Feeling.Positive,
                text = "Archive",
                icon = R.drawable.round_archive_24
            ) {
                modal.hide()
                onArchive()
            }
        }
        SpacerVer(height = 48.dp)
    }
}

private fun bodyText(
    categoryname: String,
    archived: Boolean
): String {
    val baseText =
        "DANGER! Deleting \"$categoryname\" category will make all of its transactions " +
                "\"unspecified\" (uncategorized). This operation CANNOT be undone and " +
                "will affect your statistics!" +
                " Please, be careful otherwise you may lose your data."

    val unarchivedText =
        "\n\nIf you don't want to see this category but want preserve its transactions," +
                " a better option would be to just archive it."
    return if (archived) baseText else baseText + unarchivedText
}

// region Preview
@Preview
@Composable
private fun Preview_Unarchived() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        DeleteCategoryModal(
            modal = modal,
            categoryName = "Category 1",
            archived = false,
            onArchive = {},
            onDelete = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Archived() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        DeleteCategoryModal(
            modal = modal,
            categoryName = "Category 1",
            archived = true,
            onArchive = {},
            onDelete = {}
        )
    }
}
// endregion