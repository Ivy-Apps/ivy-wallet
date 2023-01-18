package com.ivy.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.data.Theme
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.ColumnRoot
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l1_buildingBlocks.SpacerWeight
import com.ivy.design.l2_components.modal.CloseButton
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

// TODO: Not implemented, yet => Re-work it

@Composable
fun BoxScope.HomeMoreMenuScreen() {
    val viewModel: HomeMoreMenuViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel = viewModel, preview = ::previewState)

    ColumnRoot(
        modifier = Modifier
            .background(UI.colors.pure)
    ) {
        SpacerWeight(weight = 1f)
        IvyButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Positive,
            text = "Categories",
            icon = null
        ) {
            viewModel?.onEvent(MoreMenuEvent.CategoriesClick)
        }
        SpacerVer(height = 16.dp)
        IvyButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Positive,
            text = "Settings",
            icon = null
        ) {
            viewModel?.onEvent(MoreMenuEvent.SettingsClick)
        }
        SpacerVer(height = 16.dp)
        ToggleTheme(
            theme = state.theme,
            onThemeChange = {
                viewModel?.onEvent(MoreMenuEvent.ThemeChange(it))
            }
        )
        SpacerWeight(weight = 1f)
        CloseButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                viewModel?.onEvent(MoreMenuEvent.Close)
            }
        )
        SpacerVer(height = 48.dp)
    }
}

@Composable
private fun ToggleTheme(
    theme: Theme,
    onThemeChange: (Theme) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IvyButton(
            modifier = Modifier.weight(1f),
            size = ButtonSize.Big,
            visibility = if (theme == Theme.Light) Visibility.High else Visibility.Medium,
            feeling = Feeling.Positive,
            text = "Light",
            icon = null,
        ) {
            onThemeChange(Theme.Light)
        }
        SpacerHor(width = 12.dp)
        IvyButton(
            modifier = Modifier.weight(1f),
            size = ButtonSize.Big,
            visibility = if (theme == Theme.Dark) Visibility.High else Visibility.Medium,
            feeling = Feeling.Positive,
            text = "Dark",
            icon = null,
        ) {
            onThemeChange(Theme.Dark)
        }
        SpacerHor(width = 12.dp)
        IvyButton(
            modifier = Modifier.weight(1f),
            size = ButtonSize.Big,
            visibility = if (theme == Theme.Auto) Visibility.High else Visibility.Medium,
            feeling = Feeling.Positive,
            text = "Auto",
            icon = null,
        ) {
            onThemeChange(Theme.Auto)
        }
    }
}


@Preview
@Composable
private fun HomeMoreMenuPreview() {
    IvyPreview {
        HomeMoreMenuScreen()
    }
}

private fun previewState() = MoreMenuState(
    theme = Theme.Auto,
)
