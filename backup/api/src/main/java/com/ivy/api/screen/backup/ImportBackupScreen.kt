package com.ivy.api.screen.backup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.ui.rootScreen
import com.ivy.data.file.FileType
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.l3_ivyComponents.BackButton
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.ImportBackupScreen() {
    val viewModel: ImportBackupViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    UI(state = state, onEvent = viewModel::onEvent)
}

@Composable
private fun UI(
    state: ImportBackupState,
    onEvent: (ImportBackupEvent) -> Unit
) {
    ColumnRoot(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        SpacerVer(height = 16.dp)
        val notInProgress = state.progress == null || state.result != null
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (notInProgress) {
                BackButton {
                    onEvent(ImportBackupEvent.Finish)
                }
                SpacerHor(width = 16.dp)
                B1(text = "Import Backup")
            }
        }
        BackHandler(enabled = notInProgress) {
            onEvent(ImportBackupEvent.Finish)
        }

        SpacerWeight(weight = 1f)

        if (state.progress != null) {
            Progress(progress = state.progress)
        }

        if (state.result != null) {
            SpacerVer(height = 32.dp)
            Result(result = state.result)
        }

        if (notInProgress && state.result !is ImportResult.Success) {
            SpacerVer(height = 24.dp)
            val rootScreen = rootScreen()
            IvyButton(
                size = ButtonSize.Big,
                visibility = Visibility.Focused,
                feeling = Feeling.Positive,
                text = "Import Backup .zip"
            ) {
                rootScreen.fileChooser(fileType = FileType.Zip) {
                    onEvent(ImportBackupEvent.ImportFile(it))
                }
            }
        }

        if (state.result is ImportResult.Success) {
            SpacerVer(height = 12.dp)
            IvyButton(
                size = ButtonSize.Big,
                visibility = Visibility.Focused,
                feeling = Feeling.Positive,
                text = "Finish"
            ) {
                onEvent(ImportBackupEvent.Finish)
            }
        }
        SpacerWeight(weight = 1f)
    }
}

// region Progress
@Composable
private fun ColumnScope.Progress(
    progress: Progress
) {
    ProgressBar(percent = progress.percent)
    SpacerVer(height = 12.dp)
    B2(text = progress.message, color = UI.colors.orange)
}

@Composable
private fun ProgressBar(
    percent: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(UI.colors.medium, UI.shapes.rounded)
                .border(1.dp, UI.colors.primary, UI.shapes.rounded)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth(fraction = percent)
                .height(48.dp)
                .background(UI.colors.primary, UI.shapes.rounded)
        )
    }
}
// endregion

@Composable
private fun ColumnScope.Result(
    result: ImportResult
) {
    H2(
        text = result.message,
        color = when (result) {
            is ImportResult.Error -> UI.colors.red
            is ImportResult.Success -> UI.colors.green
        }
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        UI(
            state = ImportBackupState(
                progress = Progress(
                    percent = 0.65f,
                    message = "Progress message..."
                ),
                result = ImportResult.Success("Success message")
            ),
            onEvent = {}
        )
    }
}
// endregion