package com.ivy.core.ui.account.folder.choose

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.data.account.FolderUi

@Immutable
data class ChooseFolderState(
    val folders: List<FolderUi>
)