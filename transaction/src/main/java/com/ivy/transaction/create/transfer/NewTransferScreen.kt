package com.ivy.transaction.create.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.domain.pure.dummy.dummyActual
import com.ivy.core.domain.pure.format.dummyCombinedValueUi
import com.ivy.core.ui.account.create.CreateAccountModal
import com.ivy.core.ui.category.pick.CategoryPickerModal
import com.ivy.core.ui.data.account.dummyAccountUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.transaction.dummyTrnTimeActualUi
import com.ivy.core.ui.modal.RateModal
import com.ivy.design.l0_system.color.Blue2Dark
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.keyboardPadding
import com.ivy.design.util.keyboardShownState
import com.ivy.resources.R
import com.ivy.transaction.component.*
import com.ivy.transaction.create.CreateTrnFlowUiState
import com.ivy.transaction.data.TransferRateUi
import com.ivy.transaction.modal.*

@Composable
fun BoxScope.NewTransferScreen() {
    val viewModel: NewTransferViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    state.createFlow.keyboardController.wire()

    LaunchedEffect(Unit) {
        viewModel.onEvent(NewTransferEvent.Initial)
    }

    UI(state = state, onEvent = viewModel::onEvent)
}

@Composable
private fun BoxScope.UI(
    state: NewTransferState,
    onEvent: (NewTransferEvent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        item(key = "toolbar") {
            SpacerVer(height = 24.dp)
            TrnScreenToolbar(
                onClose = {
                    onEvent(NewTransferEvent.Close)
                },
                actions = {},
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
                focus = state.createFlow.titleFocus,
                onTitleChange = { onEvent(NewTransferEvent.TitleChange(it)) },
                onCta = { onEvent(NewTransferEvent.Add) }
            )
            TitleSuggestions(
                focused = titleFocused && keyboardShown,
                suggestions = state.titleSuggestions,
                onSuggestionClick = { onEvent(NewTransferEvent.TitleChange(it)) }
            )
        }
        item(key = "category") {
            SpacerVer(height = 12.dp)
            CategoryComponent(
                modifier = Modifier.padding(horizontal = 16.dp),
                category = state.category
            ) {
                state.createFlow.categoryPickerModal.show()
            }
        }
        item(key = "description") {
            SpacerVer(height = 24.dp)
            DescriptionComponent(
                modifier = Modifier.padding(horizontal = 16.dp),
                description = state.description
            ) {
                state.createFlow.descriptionModal.show()
            }
        }
        item(key = "trn_time") {
            SpacerVer(height = 12.dp)
            TrnTimeComponent(
                modifier = Modifier.padding(horizontal = 16.dp),
                extendedTrnTime = state.timeUi,
                onDateClick = {
                    state.createFlow.dateModal.show()
                },
                onTimeClick = {
                    state.createFlow.timeModal.show()
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
                    state.feeModal.show()
                }
                if (state.rate != null) {
                    SpacerHor(width = 12.dp)
                    TransferRateComponent(
                        modifier = Modifier.weight(1f),
                        rate = state.rate,
                    ) {
                        state.rateModal.show()
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
        ctaText = stringResource(R.string.add),
        ctaIcon = R.drawable.ic_round_add_24,
        onCtaClick = {
            onEvent(NewTransferEvent.Add)
        },
        onFromAccountChange = {
            onEvent(NewTransferEvent.FromAccountChange(it))
        },
        onToAccountChange = {
            onEvent(NewTransferEvent.ToAccountChange(it))
        },
        onFromAmountChange = {
            onEvent(NewTransferEvent.FromAmountChange(it))
        },
        onToAmountChange = {
            onEvent(NewTransferEvent.ToAmountChange(it))
        },
    )

    Modals(state = state, onEvent = onEvent)
}

@Composable
private fun BoxScope.Modals(
    state: NewTransferState,
    onEvent: (NewTransferEvent) -> Unit
) {
    CategoryPickerModal(
        modal = state.createFlow.categoryPickerModal,
        selected = state.category,
        trnType = null,
        onPick = {
            onEvent(NewTransferEvent.CategoryChange(it))
        }
    )

    DescriptionModal(
        modal = state.createFlow.descriptionModal,
        initialDescription = state.description,
        onDescriptionChange = {
            onEvent(NewTransferEvent.DescriptionChange(it))
        }
    )

    TrnDateModal(
        modal = state.createFlow.dateModal,
        trnTime = state.time,
        onTrnTimeChange = {
            onEvent(NewTransferEvent.TrnTimeChange(it))
        }
    )
    TrnTimeModal(
        modal = state.createFlow.timeModal,
        trnTime = state.time,
        onTrnTimeChange = {
            onEvent(NewTransferEvent.TrnTimeChange(it))
        }
    )

    // Fee modal
    FeeModal(
        modal = state.feeModal,
        fee = state.fee.value,
        onRemoveFee = {
            onEvent(NewTransferEvent.FeeChange(null))
        },
        onFeePercent = {
            onEvent(NewTransferEvent.FeePercent(it))
        },
        onFeeChange = {
            onEvent(NewTransferEvent.FeeChange(it))
        }
    )

    if (state.rate != null) {
        RateModal(
            modal = state.rateModal,
            key = "transfer_rate",
            rate = state.rate.rateValue,
            fromCurrency = state.rate.fromCurrency,
            toCurrency = state.rate.toCurrency,
            onRateChange = {
                onEvent(NewTransferEvent.RateChange(it))
            }
        )
    }

    val createAccountModal = rememberIvyModal()
    TransferAmountModal(
        modal = state.createFlow.amountModal,
        amount = state.amountFrom.value,
        fromAccount = state.accountFrom,
        toAccount = state.accountTo,
        onAddAccount = {
            createAccountModal.show()
        },
        onAmountEnter = {
            onEvent(NewTransferEvent.TransferAmountChange(it))
        },
        onFromAccountChange = {
            onEvent(NewTransferEvent.FromAccountChange(it))
        },
        onToAccountChange = {
            onEvent(NewTransferEvent.ToAccountChange(it))
        }
    )

    CreateAccountModal(
        modal = createAccountModal,
        level = 2,
    )
}

// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        UI(
            state = NewTransferState(
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
                rate = null,

                titleSuggestions = listOf("Title 1", "Title 2"),
                createFlow = CreateTrnFlowUiState.default(),
                feeModal = rememberIvyModal(),
                rateModal = rememberIvyModal(),
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
            state = NewTransferState(
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
                rate = TransferRateUi(
                    rateValue = 1.95,
                    rateValueFormatted = "1.95",
                    fromCurrency = "EUR",
                    toCurrency = "BGN",
                ),

                titleSuggestions = listOf("Title 1", "Title 2"),
                createFlow = CreateTrnFlowUiState.default(),
                feeModal = rememberIvyModal(),
                rateModal = rememberIvyModal(),
            ),
            onEvent = {}
        )
    }
}
// endregion