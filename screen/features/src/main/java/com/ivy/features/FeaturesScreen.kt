package com.ivy.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivy.design.l0_system.UI
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.style
import com.ivy.navigation.navigation
import com.ivy.navigation.screenScopedViewModel
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.components.IvySwitch
import kotlinx.collections.immutable.ImmutableList

@Composable
fun FeaturesScreenImpl(
    viewModel: FeaturesViewModel = screenScopedViewModel()
) {
    FeaturesUi(
        uiState = viewModel.uiState(),
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeaturesUi(
    uiState: FeaturesUiState,
    onEvent: (FeaturesUiEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Title()
                },
                navigationIcon = {
                    BackButton()
                },
            )
        },
        content = { innerPadding ->
            Content(
                modifier = Modifier.padding(innerPadding),
                features = uiState.features,
                onToggleFeature = {
                    onEvent(FeaturesUiEvent.ToggleFeature(it))
                }
            )
        }
    )
}

@Composable
private fun BackButton(
    modifier: Modifier = Modifier,
) {
    val nav = navigation()
    IconButton(
        modifier = modifier,
        onClick = {
            nav.back()
        }
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = "Features",
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Content(
    features: ImmutableList<Feature>,
    onToggleFeature: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(features) { index, item ->
            when (item) {
                is FeatureHeader -> {
                    FeatureSectionDivider(text = item.name)
                }

                is FeatureItem -> {
                    FeatureRow(
                        feature = item,
                        onToggleClick = { onToggleFeature(item.key) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(
    feature: FeatureItem,
    onToggleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxSize(),
        onClick = onToggleClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = feature.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W700
                )
                if (feature.description != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = feature.description,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IvySwitch(enabled = feature.enabled, onEnabledChange = { onToggleClick() })
        }
    }
}

@Composable
private fun FeatureSectionDivider(
    text: String,
    color: Color = Gray
) {
    Column {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = text,
            style = UI.typo.b2.style(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
