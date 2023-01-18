package com.ivy.transaction.edit.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.domain.pure.dummy.dummyActual
import com.ivy.core.domain.pure.format.dummyCombinedValueUi
import com.ivy.core.ui.category.pick.CategoryPickerModal
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.transaction.dummyTrnTimeActualUi
import com.ivy.core.ui.modal.RateModal
import com.ivy.design.l0_system.color.Blue2Dark
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.button.DeleteButton
import com.ivy.design.l3_ivyComponents.modal.DeleteConfirmationModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.KeyboardController
import com.ivy.design.util.keyboardPadding
import com.ivy.design.util.keyboardShownState
import com.ivy.resources.R
import com.ivy.transaction.component.*
import com.ivy.transaction.data.TransferRateUi
import com.ivy.transaction.modal.DescriptionModal
import com.ivy.transaction.modal.FeeModal
import com.ivy.transaction.modal.TrnDateModal
import com.ivy.transaction.modal.TrnTimeModal

@Composable
fun BoxScope.EditTransferScreen(
    batchId: String,
) {
    val viewModel: EditTransferViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(EditTransferEvent.Initial(batchId = batchId))
    }

    UI(state = state, onEvent = viewModel::onEvent)
}

@Composable
private fun BoxScope.UI(
    state: EditTransferState,
    onEvent: (EditTransferEvent) -> Unit,
) {
    val dateModal = rememberIvyModal()
    val timeModal = rememberIvyModal()
    val categoryPickerModal = rememberIvyModal()
    val descriptionModal = rememberIvyModal()
    val deleteConfirmationModal = rememberIvyModal()
    val feeModal = rememberIvyModal()
    val rateModal = rememberIvyModal()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        item(key = "toolbar") {
            SpacerVer(height = 24.dp)
            TrnScreenToolbar(
                onClose = {
                    onEvent(EditTransferEvent.Close)
                },
                actions = {},
            )
        }
        item(key = "title") {
            SpacerVer(height = 24.dp)
            val titleFocus = remember { FocusRequester() }
            var titleFocused by remember { mutableStateOf(false) }
            val keyboardShown by keyboardShownState()
            TitleInput(
                modifier = Modifier.onFocusChanged {
                    titleFocused = it.isFocused || it.hasFocus
                },
                title = state.title,
                focus = titleFocus,
                onTitleChange = { onEvent(EditTransferEvent.TitleChange(it)) },
                onCta = { onEvent(EditTransferEvent.Save) }
            )
            TitleSuggestions(
                focused = titleFocused && keyboardShown,
                suggestions = state.titleSuggestions,
                onSuggestionClick = { onEvent(EditTransferEvent.TitleChange(it)) }
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
                onTimeClick = {
                    timeModal.show()
                },
                onDateClick = {
                    dateModal.show()
                }
            )
        }
        item(key = "fee_rate") {
            SpacerVer(height = 12.dp)
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeeComponent(
                    fee = state.fee.valueUi,
                    validFee = state.fee.value.amount > 0
                ) {
                    feeModal.show()
                }
                if (state.rate != null) {
                    SpacerHor(width = 12.dp)
                    TransferRateComponent(
                        modifier = Modifier.weight(1f),
                        rate = state.rate,
                    ) {
                        rateModal.show()
                    }
                }
            }
        }
        item(key = "last_item_spacer") {
            val keyboardShown by keyboardShownState()
            if (keyboardShown) {
                SpacerVer(height = keyboardPadding())
            }
            // To account for bottom sheet's height
            SpacerVer(height = 520.dp)
        }
    }

    TransferBottomSheet(
        accountFrom = state.accountFrom,
        amountFromUi = state.amountFrom.valueUi,
        amountFrom = state.amountFrom.value,
        accountTo = state.accountTo,
        amountToUi = state.amountTo.valueUi,
        amountTo = state.amountTo.value,
        ctaText = stringResource(R.string.save),
        ctaIcon = R.drawable.round_done_24,
        secondaryActions = {
            DeleteButton {
                deleteConfirmationModal.show()
            }
            SpacerHor(width = 12.dp)
        },
        onCtaClick = {
            onEvent(EditTransferEvent.Save)
        },
        onFromAccountChange = {
            onEvent(EditTransferEvent.FromAccountChange(it))
        },
        onToAccountChange = {
            onEvent(EditTransferEvent.ToAccountChange(it))
        },
        onFromAmountChange = {
            onEvent(EditTransferEvent.FromAmountChange(it))
        },
        onToAmountChange = {
            onEvent(EditTransferEvent.ToAmountChange(it))
        },
    )

    Modals(
        state = state,
        dateModal = dateModal,
        timeModal = timeModal,
        descriptionModal = descriptionModal,
        categoryPickerModal = categoryPickerModal,
        deleteConfirmationModal = deleteConfirmationModal,
        feeModal = feeModal,
        rateModal = rateModal,
        onEvent = onEvent
    )
}

@Composable
private fun BoxScope.Modals(
    state: EditTransferState,
    dateModal: IvyModal,
    timeModal: IvyModal,
    descriptionModal: IvyModal,
    categoryPickerModal: IvyModal,
    deleteConfirmationModal: IvyModal,
    feeModal: IvyModal,
    rateModal: IvyModal,
    onEvent: (EditTransferEvent) -> Unit
) {
    CategoryPickerModal(
        modal = categoryPickerModal,
        selected = state.category,
        trnType = null,
        onPick = {
            onEvent(EditTransferEvent.CategoryChange(it))
        }
    )

    DescriptionModal(
        modal = descriptionModal,
        initialDescription = state.description,
        onDescriptionChange = {
            onEvent(EditTransferEvent.DescriptionChange(it))
        }
    )

    TrnDateModal(
        modal = dateModal,
        trnTime = state.time,
        onTrnTimeChange = {
            onEvent(EditTransferEvent.TrnTimeChange(it))
        }
    )
    TrnTimeModal(
        modal = timeModal,
        trnTime = state.time,
        onTrnTimeChange = {
            onEvent(EditTransferEvent.TrnTimeChange(it))
        }
    )

    // Fee modal
    FeeModal(
        modal = feeModal,
        fee = state.fee.value,
        onRemoveFee = {
            onEvent(EditTransferEvent.FeeChange(null))
        },
        onFeePercent = {
            onEvent(EditTransferEvent.FeePercent(it))
        },
        onFeeChange = {
            onEvent(EditTransferEvent.FeeChange(it))
        }
    )

    if (state.rate != null) {
        RateModal(
            modal = rateModal,
            key = "transfer_rate",
            rate = state.rate.rateValue,
            fromCurrency = state.rate.fromCurrency,
            toCurrency = state.rate.toCurrency,
            onRateChange = {
                onEvent(EditTransferEvent.RateChange(it))
            }
        )
    }

    DeleteConfirmationModal(modal = deleteConfirmationModal) {
        onEvent(EditTransferEvent.Delete)
    }
}

// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        UI(
            state = EditTransferState(
                accountFrom = dummyAccountUi(
                    name = "Personal Bank",
                    color = Blue2Dark,
                ),
                amountFrom = dummyCombinedValueUi(),
                accountTo = dummyAccountUi(name = "Cash"),
                amountTo = dummyCombinedValueUi(),
                category = dummyCategoryUi(),
                description = null,
                timeUi = dummyTrnTimeActualUi(),
                time = dummyActual(),
                title = null,
                fee = dummyCombinedValueUi(),
                rate = TransferRateUi(
                    rateValueFormatted = "1.96",
                    rateValue = 1.95583,
                    fromCurrency = "EUR",
                    toCurrency = "BGN"
                ),

                titleSuggestions = listOf("Title 1", "Title 2"),
                keyboardController = KeyboardController(),
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
            state = EditTransferState(
                accountFrom = dummyAccountUi(
                    name = "Personal Bank",
                    color = Blue2Dark,
                ),
                amountFrom = dummyCombinedValueUi(amount = 400.0),
                accountTo = dummyAccountUi(name = "Cash"),
                amountTo = dummyCombinedValueUi(amount = 400.0),
                category = dummyCategoryUi(),
                description = "Need some cash",
                timeUi = dummyTrnTimeActualUi(),
                time = dummyActual(),
                title = "ATM Withdrawal",
                fee = dummyCombinedValueUi(amount = 2.0),
                rate = null,

                titleSuggestions = listOf("Title 1", "Title 2"),
                keyboardController = KeyboardController(),
            ),
            onEvent = {}
        )
    }
}
// endregion