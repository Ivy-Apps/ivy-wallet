package com.ivy.transaction.create

import androidx.compose.runtime.Immutable
import androidx.compose.ui.focus.FocusRequester
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.util.KeyboardController
import com.ivy.transaction.create.action.CreateTrnStepsAct
import com.ivy.transaction.create.data.CreateTrnFlow
import com.ivy.transaction.create.data.CreateTrnStep
import javax.inject.Inject

class CreateTrnController @Inject constructor(
    private val createTrnStepsAct: CreateTrnStepsAct,
) {
    val uiFlow: CreateTrnFlowUiState = CreateTrnFlowUiState.default()
    private var createTrnFlow: CreateTrnFlow? = null

    private val titleStep = object : FlowStep {
        override fun execute() {
            uiFlow.titleFocus.requestFocus()
            uiFlow.keyboardController.show()
        }
    }
    private val amountStep = ModalStep(uiFlow.amountModal)
    private val categoryStep = ModalStep(uiFlow.categoryPickerModal)
    private val accountStep = ModalStep(uiFlow.accountPickerModal)
    private val descriptionStep = ModalStep(uiFlow.descriptionModal)
    private val dateStep = ModalStep(uiFlow.dateModal)
    private val timeStep = ModalStep(uiFlow.timeModal)

    private fun flowStep(step: CreateTrnStep): FlowStep = when (step) {
        CreateTrnStep.Title -> titleStep
        CreateTrnStep.Amount -> amountStep
        CreateTrnStep.Category -> categoryStep
        CreateTrnStep.Account -> accountStep
        CreateTrnStep.Description -> descriptionStep
        CreateTrnStep.Date -> dateStep
        CreateTrnStep.Time -> timeStep
    }

    // region Public
    suspend fun startFlow() {
        val createTrnFlow = createTrnStepsAct(Unit).also {
            this.createTrnFlow = it
        }
        flowStep(createTrnFlow.first).execute()
    }

    fun nextStep(after: CreateTrnStep) {
        createTrnFlow?.steps?.get(after)?.let(::flowStep)?.execute()
    }

    fun hideKeyboard() {
        uiFlow.keyboardController.hide()
    }
    // endregion


    // region Helper classes
    private interface FlowStep {
        fun execute()
    }

    class ModalStep(private val modal: IvyModal) : FlowStep {
        override fun execute() {
            modal.show()
        }
    }
    // endregion
}

// It would be better to be nested class
// But I'm not sure if it's Compose optimized that way
@Immutable
data class CreateTrnFlowUiState(
    val keyboardController: KeyboardController,
    val titleFocus: FocusRequester,
    val amountModal: IvyModal,
    val categoryPickerModal: IvyModal,
    val accountPickerModal: IvyModal,
    val descriptionModal: IvyModal,
    val dateModal: IvyModal,
    val timeModal: IvyModal,
) {
    companion object {
        fun default() = CreateTrnFlowUiState(
            keyboardController = KeyboardController(),
            titleFocus = FocusRequester(),
            amountModal = IvyModal(),
            categoryPickerModal = IvyModal(),
            accountPickerModal = IvyModal(),
            descriptionModal = IvyModal(),
            dateModal = IvyModal(),
            timeModal = IvyModal(),
        )
    }
}