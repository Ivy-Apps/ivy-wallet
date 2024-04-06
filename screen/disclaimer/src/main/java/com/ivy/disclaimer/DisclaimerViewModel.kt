package com.ivy.disclaimer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.ivy.data.repository.LegalRepository
import com.ivy.navigation.Navigation
import com.ivy.ui.ComposeViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

class DisclaimerViewModel @Inject constructor(
    private val navigation: Navigation,
    private val legalRepo: LegalRepository,
) : ComposeViewModel<DisclaimerViewState, DisclaimerViewEvent>() {

    private var checkboxes by mutableStateOf(LegalCheckboxes)

    @Composable
    override fun uiState(): DisclaimerViewState {
        return DisclaimerViewState(
            checkboxes = checkboxes,
            agreeButtonEnabled = checkboxes.all(CheckboxViewState::checked),
        )
    }

    override fun onEvent(event: DisclaimerViewEvent) {
        when (event) {
            DisclaimerViewEvent.OnAgreeClick -> handleAgreeClick()
            is DisclaimerViewEvent.OnCheckboxClick -> handleCheckboxClick(event)
            DisclaimerViewEvent.OnExportDataClick -> handleExportDataClick()
        }
    }

    private fun handleAgreeClick() {
        viewModelScope.launch {
            legalRepo.setDisclaimerAccepted(accepted = true)
            navigation.back()
        }
    }

    private fun handleCheckboxClick(event: DisclaimerViewEvent.OnCheckboxClick) {
        checkboxes = checkboxes.mapIndexed { index, item ->
            if (index == event.index) {
                item.copy(
                    checked = !item.checked
                )
            } else {
                item
            }
        }.toImmutableList()
    }

    private fun handleExportDataClick() {
        // TODO: Implement
    }

    companion object {
        val LegalCheckboxes = // LEGAL TEXT:
            // Do NOT extract or translate these strings
            listOf(
                CheckboxViewState(
                    text = """
                    I understand this app is open-source and community-driven, 
                    provided 'as-is' without warranties of any kind. 
                    I acknowledge the app may have errors and agree to use it at my own risk.
                """.trimIndent(),
                    checked = false,
                ),
                CheckboxViewState(
                    text = """
                    I acknowledge that no warranty, express or implied, 
                    is made regarding the accuracy, reliability, 
                    or completeness of the data provided. 
                    I am responsible for backing up my data to prevent loss.
                """.trimIndent(),
                    checked = false,
                ),
                CheckboxViewState(
                    text = """
                  I agree the app developers, contributors, and the distributing company shall 
                  not be liable for any claims, damages, 
                  or data loss resulting from my use of the app, including security breaches.  
                """.trimIndent(),
                    checked = false,
                ),
                CheckboxViewState(
                    text = """
                    I am aware the app might display misleading information or inaccuracies. 
                    I accept responsibility for verifying any financial information 
                    or calculations before making decisions based on app data.
                """.trimIndent(),
                    checked = false,
                ),
            ).toImmutableList()
    }
}