package com.ivy.core.ui.account.folder.pick

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.account.FolderUi

@Immutable
data class FolderPickerState(
    val folders: List<FolderUi>
)