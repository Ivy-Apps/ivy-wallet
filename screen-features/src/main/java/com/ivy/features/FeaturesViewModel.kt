package com.ivy.features

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.ivy.domain.ComposeViewModel
import com.ivy.domain.features.Features
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            features = getFeatures()
        )
    }

    @Composable
    fun getFeatures(): ImmutableList<FeatureUi> {
        val allFeatures = features.allFeatures.map {
            FeatureUi(
                name = it.name ?: it.key,
                description = it.description,
                enabled = it.asEnabledState()
            )
        }
        return allFeatures.toImmutableList()
    }

    override fun onEvent(event: FeaturesUiEvent) {
        when (event) {
            is FeaturesUiEvent.ToggleFeature -> toggleFeature(event)
        }
    }

    private fun toggleFeature(event: FeaturesUiEvent.ToggleFeature) {
        viewModelScope.launch {
            val feature = features.allFeatures[event.index]
            val enabled = feature.enabled(context).first() ?: false
            feature.set(context, !enabled)
        }
    }
}