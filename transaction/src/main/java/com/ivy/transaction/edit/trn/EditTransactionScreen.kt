package com.ivy.transaction.edit.trn

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.domain.pure.format.dummyCombinedValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import com.ivy.core.ui.category.pick.CategoryPickerModal
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.transaction.dummyTrnTimeActualUi
import com.ivy.core.ui.data.transaction.dummyTrnTimeDueUi
import com.ivy.core.ui.transaction.feeling
import com.ivy.core.ui.transaction.humanText
import com.ivy.core.ui.transaction.icon
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.dummyTrnTimeActual
import com.ivy.data.transaction.dummyTrnTimeDue
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.SwitchRow
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.DeleteButton
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.l3_ivyComponents.modal.DeleteConfirmationModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.KeyboardController
import com.ivy.design.util.keyboardPadding
import com.ivy.design.util.keyboardShownState
import com.ivy.resources.R
import com.ivy.transaction.component.*
import com.ivy.transaction.modal.DescriptionModal
import com.ivy.transaction.modal.TrnDateModal
import com.ivy.transaction.modal.TrnTimeModal
import com.ivy.transaction.modal.TrnTypeModal

@Composable
fun BoxScope.EditTransactionScreen(trnId: String) {
    val viewModel: EditTransactionViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    state.keyboardController.wire()

    LaunchedEffect(Unit) {
        viewModel.onEvent(EditTrnEvent.Initial(trnId = trnId))
    }

    UI(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun BoxScope.UI(
    state: EditTrnState,
    onEvent: (EditTrnEvent) -> Unit,
) {
    val trnTypeModal = rememberIvyModal()
    val dateModal = rememberIvyModal()
    val timeModal = rememberIvyModal()
    val accountPickerModal = rememberIvyModal()
    val categoryPickerModal = rememberIvyModal()
    val descriptionModal = rememberIvyModal()
    val amountModal = rememberIvyModal()
    val deleteConfirmationModal = rememberIvyModal()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        item(key = "toolbar") {
            SpacerVer(height = 24.dp)
            EditTrnScreenToolbar(
                onClose = {
                    onEvent(EditTrnEvent.Close)
                },
                trnType = state.trnType,
                onChangeTrnType = {
                    trnTypeModal.show()
                }
            )
        }
        item(key = "title") {
            SpacerVer(height = 24.dp)
            var titleFocused by remember { mutableStateOf(false) }
            val keyboardShown by keyboardShownState()
            TitleInput(
                modifier = Modifier.onFocusChanged {
                    titleFocused = it.isFocused || it.hasFocus
                },
                title = state.title,
                focus = remember { FocusRequester() },
                onTitleChange = { onEvent(EditTrnEvent.TitleChange(it)) },
                onCta = { onEvent(EditTrnEvent.Save) }
            )
            TitleSuggestions(
                focused = titleFocused && keyboardShown,
                suggestions = state.titleSuggestions,
                onSuggestionClick = { onEvent(EditTrnEvent.TitleChange(it)) }
            )
        }
        item(key = "category") {
            SpacerVer(height = 12.dp)
            CategoryComponent(
                modifier = Modifier.padding(horizontal = 16.dp),
                category = state.category
            ) {
                categoryPickerModal.show()
            }
        }
        item(key = "description") {
            SpacerVer(height = 24.dp)
            DescriptionComponent(
                modifier = Modifier.padding(horizontal = 16.dp),
                description = state.description
            ) {
                descriptionModal.show()
            }
        }
        item(key = "trn_time") {
            SpacerVer(height = 12.dp)
            TrnTimeComponent(
                modifier = Modifier.padding(horizontal = 16.dp),
                extendedTrnTime = state.timeUi,
                onDateClick = { dateModal.show() },
                onTimeClick = { timeModal.show() }
            )
        }
        item(key = "hidden_switch") {
            SpacerVer(height = 12.dp)
            SwitchRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(2.dp, UI.colors.primary, UI.shapes.fullyRounded),
                enabled = state.hidden,
                text = "Hide transaction",
                onValueChange = {
                    onEvent(EditTrnEvent.HiddenChange(it))
                }
            )
        }
        item(key = "last_item_spacer") {
            val keyboardShown by keyboardShownState()
            if (keyboardShown) {
                SpacerVer(height = keyboardPadding())
            }
            // To account for "Amount Account sheet" height
            SpacerVer(height = 480.dp)
        }
    }

    AmountAccountSheet(
        amountUi = state.amount.valueUi,
        amount = state.amount.value,
        amountBaseCurrency = state.amountBaseCurrency,
        account = state.account,
        ctaText = stringResource(R.string.save),
        ctaIcon = R.drawable.round_done_24,
        accountPickerModal = accountPickerModal,
        amountModal = amountModal,
        secondaryActions = {
            DeleteButton {
                deleteConfirmationModal.show()
            }
            SpacerHor(width = 12.dp)
        },
        onAccountChange = {
            onEvent(EditTrnEvent.AccountChange(it))
        },
        onAmountEnter = {
            onEvent(EditTrnEvent.AmountChange(it))
        },
        onCtaClick = {
            onEvent(EditTrnEvent.Save)
        }
    )

    Modals(
        state = state,
        trnTypeModal = trnTypeModal,
        dateModal = dateModal,
        timeModal = timeModal,
        descriptionModal = descriptionModal,
        categoryPickerModal = categoryPickerModal,
        deleteConfirmationModal = deleteConfirmationModal,
        onEvent = onEvent
    )
}

@Composable
private fun BoxScope.Modals(
    state: EditTrnState,
    categoryPickerModal: IvyModal,
    descriptionModal: IvyModal,
    trnTypeModal: IvyModal,
    dateModal: IvyModal,
    timeModal: IvyModal,
    deleteConfirmationModal: IvyModal,
    onEvent: (EditTrnEvent) -> Unit
) {
    CategoryPickerModal(
        modal = categoryPickerModal,
        selected = state.category,
        trnType = state.trnType,
        onPick = {
            onEvent(EditTrnEvent.CategoryChange(it))
        }
    )

    DescriptionModal(
        modal = descriptionModal,
        initialDescription = state.description,
        onDescriptionChange = {
            onEvent(EditTrnEvent.DescriptionChange(it))
        }
    )

    TrnTypeModal(
        modal = trnTypeModal,
        trnType = state.trnType,
        onTransactionTypeChange = {
            onEvent(EditTrnEvent.TrnTypeChange(it))
        }
    )

    TrnDateModal(
        modal = dateModal,
        trnTime = state.time,
        onTrnTimeChange = {
            onEvent(EditTrnEvent.TrnTimeChange(it))
        }
    )
    TrnTimeModal(
        modal = timeModal,
        trnTime = state.time,
        onTrnTimeChange = {
            onEvent(EditTrnEvent.TrnTimeChange(it))
        }
    )

    DeleteConfirmationModal(modal = deleteConfirmationModal) {
        onEvent(EditTrnEvent.Delete)
    }
}

@Composable
private fun EditTrnScreenToolbar(
    onClose: () -> Unit,
    trnType: TransactionType,
    onChangeTrnType: () -> Unit,
) {
    TrnScreenToolbar(
        onClose = onClose,
        actions = {
            IvyButton(
                size = ButtonSize.Small,
                visibility = Visibility.Medium,
                feeling = trnType.feeling(),
                text = trnType.humanText(),
                icon = trnType.icon(),
                onClick = onChangeTrnType,
            )
        }
    )
}


// region Preview
@Preview
@Composable
private fun Preview_Empty() {
    IvyPreview {
        UI(
            state = EditTrnState(
                trnType = TransactionType.Income,
                category = null,
                description = null,
                amount = dummyCombinedValueUi(),
                amountBaseCurrency = null,
                account = dummyAccountUi(),
                title = null,
                hidden = false,

                keyboardController = KeyboardController(),
                titleSuggestions = emptyList(),
                timeUi = dummyTrnTimeActualUi(),
                time = dummyTrnTimeActual(),
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Filled() {
    IvyPreview {
        UI(
            state = EditTrnState(
                trnType = TransactionType.Expense,
                title = "Tabu Shisha",
                category = dummyCategoryUi(),
                description = "Lorem ipsum blablablabla okay good test\n1\n2\n",
                amount = dummyCombinedValueUi(amount = 23.99),
                amountBaseCurrency = dummyValueUi(amount = "48.23", currency = "BGN"),
                account = dummyAccountUi(),
                hidden = true,

                titleSuggestions = emptyList(),
                keyboardController = KeyboardController(),

                timeUi = dummyTrnTimeDueUi(),
                time = dummyTrnTimeDue(),
            ),
            onEvent = {}
        )
    }
}
// endregion