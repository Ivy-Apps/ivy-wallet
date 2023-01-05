package com.ivy.core.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.ivy.core.domain.HandlerViewModel
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l3_ivyComponents.BackButton
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.navigation.Navigator
import com.ivy.resources.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun BoxScope.ScreenBottomBar(
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .systemBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .zIndex(500f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val viewModel: BottomBarViewModel? = hiltViewModelPreviewSafe()
        BackButton(
            modifier = Modifier
        ) {
            viewModel?.onEvent(BottomBarEvent.Back)
        }
        SpacerWeight(weight = 1f)
        actions()
    }
}

private sealed interface BottomBarEvent {
    object Back : BottomBarEvent
}

@HiltViewModel
private class BottomBarViewModel @Inject constructor(
    private val navigator: Navigator,
) : HandlerViewModel<BottomBarEvent>() {
    override suspend fun handleEvent(event: BottomBarEvent) = when (event) {
        BottomBarEvent.Back -> handleBack()
    }

    private fun handleBack() {
        navigator.back()
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        ScreenBottomBar {
            IvyButton(
                size = ButtonSize.Small,
                visibility = Visibility.High,
                feeling = Feeling.Positive,
                text = "New category",
                icon = R.drawable.ic_round_add_24
            ) {

            }
        }
    }
}
// endregion