package com.ivy.photo.frame

import android.graphics.Bitmap
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ivy.core.ui.rootScreen
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.photo.frame.data.MessageUi

@Composable
fun BoxScope.AddFrameScreen() {
    val viewModel: AddFrameViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    UI(state = state, onEvent = viewModel::onEvent)
}

@Composable
private fun UI(
    state: AddFrameState,
    onEvent: (AddFrameEvent) -> Unit
) {
    ColumnRoot(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpacerWeight(weight = 1f)
        val rootScreen = rootScreen()

        IvyButton(
            size = ButtonSize.Big,
            visibility = if (state.photoWithFrame == null) Visibility.Focused else Visibility.Medium,
            feeling = Feeling.Positive,
            text = "Pick a photo"
        ) {
            rootScreen.fileChooser {
                onEvent(AddFrameEvent.PhotoChanged(it))
            }
        }

        if (state.message != MessageUi.None) {
            SpacerVer(height = 12.dp)
            when (val msg = state.message) {
                is MessageUi.Error -> B1(text = msg.message, color = UI.colors.red)
                is MessageUi.Loading -> B1(text = msg.message, color = UI.colors.orange)
                MessageUi.None -> {}
                is MessageUi.Success -> B1(text = msg.message, color = UI.colors.green)
            }
            SpacerVer(height = 12.dp)
        }

        if (state.photoWithFrame != null) {
            SpacerVer(height = 24.dp)
            PhotoWithFrame(photo = state.photoWithFrame)
            SpacerVer(height = 12.dp)
            IvyButton(
                size = ButtonSize.Big,
                visibility = Visibility.Focused,
                feeling = Feeling.Positive,
                text = "Save photo"
            ) {
                rootScreen.createFile(fileName = "IvyPhotoWithFrame.png") {
                    onEvent(AddFrameEvent.SavePhoto(it))
                }
            }
        }

        SpacerWeight(weight = 1f)
    }
}

@Composable
private fun PhotoWithFrame(
    photo: Bitmap
) {
    AsyncImage(
        modifier = Modifier.size(400.dp),
        model = photo,
        contentDescription = "photo with frame"
    )
}