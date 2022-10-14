package com.ivy.design.l3_ivyComponents.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.design.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.util.ComponentPreview

@Composable
fun ArchiveButton(
    archived: Boolean,
    color: Color = UI.colors.primary,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit
) {
    IvyButton(
        size = ButtonSize.Small,
        visibility = Visibility.Medium,
        feeling = Feeling.Custom(color),
        text = null,
        icon = if (archived) R.drawable.round_unarchive_24 else R.drawable.round_archive_24
    ) {
        if (archived) onUnarchive() else onArchive()
    }
}


// region Preview
@Preview
@Composable
private fun Preview_Archive() {
    ComponentPreview {
        ArchiveButton(archived = false, onArchive = {}, onUnarchive = {})
    }
}

@Preview
@Composable
private fun Preview_Unarchive() {
    ComponentPreview {
        ArchiveButton(archived = true, onArchive = {}, onUnarchive = {})
    }
}
// endregion