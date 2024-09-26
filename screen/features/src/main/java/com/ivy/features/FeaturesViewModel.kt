package com.ivy.features

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.ivy.domain.features.BoolFeature
import com.ivy.domain.features.FeatureGroup
import com.ivy.ui.ComposeViewModel
import com.ivy.domain.features.Features
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@SuppressLint("StaticFieldLeak")
@HiltViewModel
class FeaturesViewModel @Inject constructor(
    private val features: Features,
    @ApplicationContext
    private val context: Context
) : ComposeViewModel<FeaturesUiState, FeaturesUiEvent>() {

    @Composable
    override fun uiState(): FeaturesUiState {
        return FeaturesUiState(
            featureItemViewStates = getFeatures()
        )
    }

    @SuppressLint("BuildListAdds")
    @Composable
    fun getFeatures(): ImmutableList<FeatureItemViewState> {
        return buildList {
            val groups: Map<FeatureGroup?, BoolFeature> =
                features.allFeatures
                    .associateBy { it.group }
                    .toSortedMap(compareBy { it?.name })

            groups.forEach { group ->
                add(
                    FeatureItemViewState.FeatureHeaderViewState(
                        name = group.key?.name ?: "Undefined"
                    )
                )
                val featuresByGroup: List<FeatureItemViewState> = features
                    .allFeatures
                    .filter { it.group?.name == group.key?.name }
                    .map {
                        FeatureItemViewState.FeatureToggleViewState(
                            key = it.key,
                            name = it.name ?: it.key,
                            description = it.description,
                            enabled = it.asEnabledState()
                        )
                    }
                addAll(featuresByGroup)
            }
        }.toImmutableList()
    }

    override fun onEvent(event: FeaturesUiEvent) {
        when (event) {
            is FeaturesUiEvent.ToggleFeature -> toggleFeature(event)
        }
    }

    private fun toggleFeature(event: FeaturesUiEvent.ToggleFeature) {
        viewModelScope.launch {
            val feature = features.allFeatures.find { feature -> feature.key == event.key }
            val enabled = feature?.enabledFlow(context)?.first() ?: false
            feature?.set(context, !enabled)
        }
    }
}
