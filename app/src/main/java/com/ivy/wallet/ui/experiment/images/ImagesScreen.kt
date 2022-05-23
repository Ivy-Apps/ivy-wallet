package com.ivy.wallet.ui.experiment.images

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.ivy.design.l0_system.*
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.IvyText
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.Button
import com.ivy.frp.view.navigation.Screen
import com.ivy.wallet.R
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.architecture.FRP

class ImagesScreen : Screen

@Composable
fun BoxWithConstraintsScope.ImagesScreen(screen: ImagesScreen) {
    FRP<ImagesState, ImagesEvent, ImagesViewModel>(
        initialEvent = ImagesEvent.LoadImages
    ) { state, onEvent ->
        UI(
            state = state,
            onEvent = onEvent
        )
    }
}

@Composable
private fun UI(
    state: ImagesState,

    onEvent: (ImagesEvent) -> Unit
) {
    ColumnRoot(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        SpacerVer(height = 16.dp)

        IvyText(text = "Images Experiment", typo = UI.typo.h2)

        when (state) {
            ImagesState.Loading -> LoadingUI()
            is ImagesState.Error -> ErrorUI(state, onEvent)
            is ImagesState.Success -> SuccessUI(state = state, onEvent)
        }
    }
}

@Composable
private fun ColumnScope.LoadingUI() {
    SpacerWeight(weight = 1f)

    IvyText(
        modifier = Modifier.fillMaxWidth(),
        text = "Loading...".uppercase(),
        typo = UI.typo.nH1.style(
            color = Orange,
            textAlign = TextAlign.Center
        )
    )

    SpacerWeight(weight = 1f)
}

@Composable
private fun ColumnScope.ErrorUI(
    state: ImagesState.Error,
    onEvent: (ImagesEvent) -> Unit
) {
    SpacerWeight(weight = 1f)

    IvyText(text = "Error", typo = UI.typo.nB1.colorAs(Red))

    SpacerVer(height = 16.dp)

    IvyText(
        text = "Error msg: ${
            state.errMsg
        }", typo = UI.typo.b2
    )

    SpacerVer(height = 24.dp)

    Button(text = "Try again") {
        onEvent(ImagesEvent.LoadImages)
    }

    SpacerWeight(weight = 1f)
}

@Composable
private fun SuccessUI(
    state: ImagesState.Success,

    onEvent: (ImagesEvent) -> Unit
) {
    SpacerVer(height = 24.dp)

    LazyColumn {

        item {
            Button(text = "Load images") {
                onEvent(ImagesEvent.LoadImages)
            }

            SpacerVer(height = 16.dp)
        }

        items(
            items = state.urls,
            key = { it }
        ) { imageUrl ->
            ImageItem(imageUrl = imageUrl)

            SpacerVer(height = 8.dp)
        }
    }
}

@Composable
private fun ImageItem(
    imageUrl: String
) {
    var imgState: AsyncImagePainter.State by remember {
        mutableStateOf(AsyncImagePainter.State.Empty)
    }

    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(UI.shapes.r4),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .error(R.drawable.fingerprint_dialog_error)
            .placeholder(R.drawable.ic_vue_media_image)
            .build(),
        onState = {
            imgState = it
        },
        contentScale = ContentScale.Inside,
        contentDescription = "image"
    )

    when (imgState) {
        is AsyncImagePainter.State.Error -> {
            IvyText(text = "Couldn't load: $imageUrl".take(100), typo = UI.typo.c.colorAs(Red))
        }
        else -> {
            //do nothing
        }
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    IvyWalletPreview {
        UI(
            state = ImagesState.Loading,
            onEvent = {}
        )
    }
}