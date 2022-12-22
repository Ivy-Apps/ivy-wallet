package com.ivy.transaction.create.trn

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.core.domain.pure.dummy.dummyValue
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
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.input.InputFieldType
import com.ivy.design.l2_components.input.IvyInputField
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.navigation.destinations.transaction.NewTransaction
import com.ivy.resources.R
import com.ivy.transaction.component.*
import com.ivy.transaction.modal.DescriptionModal
import com.ivy.transaction.modal.TrnTypeModal

@Composable
fun BoxScope.NewTransactionScreen(arg: NewTransaction.Arg) {
    val viewModel: NewTransactionViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    UI(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun BoxScope.UI(
    state: NewTrnState,
    onEvent: (NewTrnEvent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        item(key = "toolbar") {
            SpacerVer(height = 24.dp)
            NewTrnScreenToolbar(
                onClose = {
                    onEvent(NewTrnEvent.Close)
                },
                trnType = state.trnType,
                onChangeTrnType = {
                    state.trnTypeModal.show()
                }
            )
        }
        item(key = "title") {
            SpacerVer(height = 24.dp)
            IvyInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                type = InputFieldType.SingleLine,
                initialValue = "",
                placeholder = "Title",
                onValueChange = {
                    onEvent(NewTrnEvent.TitleChange(it))
                }
            )
        }
        item(key = "category") {
            SpacerVer(height = 12.dp)
            CategoryComponent(
                modifier = Modifier.padding(horizontal = 16.dp),
                category = state.category
            ) {
                state.categoryPickerModal.show()
            }
        }
        item(key = "description") {
            SpacerVer(height = 24.dp)
            DescriptionComponent(
                modifier = Modifier.padding(horizontal = 16.dp),
                description = state.description
            ) {
                state.descriptionModal.show()
            }
        }
        item(key = "trn_time") {
            SpacerVer(height = 12.dp)
            TrnTimeComponent(
                modifier = Modifier.padding(horizontal = 16.dp),
                trnTime = state.time
            ) {
                state.trnTimeModal.show()
            }
        }
        item(key = "last_item_spacer") {
            SpacerVer(height = 48.dp)
        }
    }

    AmountAccountSheet(
        amountUi = state.amountUi,
        amount = state.amount,
        account = state.account,
        ctaText = stringResource(R.string.add),
        ctaIcon = R.drawable.ic_round_add_24,
        accountPickerModal = state.accountPickerModal,
        amountModal = state.amountModal,
        onAccountChange = {
            onEvent(NewTrnEvent.AccountChange(it))
        },
        onAmountEnter = {
            onEvent(NewTrnEvent.AmountChange(it))
        },
        onCtaClick = {
            onEvent(NewTrnEvent.Add)
        }
    )

    Modals(state = state, onEvent = onEvent)
}

@Composable
private fun BoxScope.Modals(
    state: NewTrnState,
    onEvent: (NewTrnEvent) -> Unit
) {
    CategoryPickerModal(
        modal = state.categoryPickerModal,
        selected = state.category,
        onPick = {
            onEvent(NewTrnEvent.CategoryChange(it))
        }
    )

    DescriptionModal(
        modal = state.descriptionModal,
        initialDescription = state.description,
        onDescriptionChange = {
            onEvent(NewTrnEvent.DescriptionChange(it))
        }
    )

    TrnTypeModal(
        modal = state.trnTypeModal,
        trnType = state.trnType,
        onTransactionTypeChange = {
            onEvent(NewTrnEvent.TrnTypeChange(it))
        }
    )
}

@Composable
private fun NewTrnScreenToolbar(
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
            state = NewTrnState(
                trnType = TransactionType.Income,
                category = null,
                description = null,
                amountUi = dummyValueUi(),
                amount = dummyValue(),
                account = dummyAccountUi(),
                title = null,

                titleFocus = remember { FocusRequester() },
                time = dummyTrnTimeActualUi(),
                trnTypeModal = rememberIvyModal(),
                categoryPickerModal = rememberIvyModal(),
                accountPickerModal = rememberIvyModal(),
                descriptionModal = rememberIvyModal(),
                trnTimeModal = rememberIvyModal(),
                amountModal = rememberIvyModal(),
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
            state = NewTrnState(
                trnType = TransactionType.Expense,
                title = "Tabu Shisha",
                category = dummyCategoryUi(),
                description = "Lorem ipsum blablablabla okay good test\n1\n2\n",
                amountUi = dummyValueUi(),
                amount = dummyValue(),
                account = dummyAccountUi(),

                titleFocus = remember { FocusRequester() },
                time = dummyTrnTimeDueUi(),
                trnTypeModal = rememberIvyModal(),
                categoryPickerModal = rememberIvyModal(),
                accountPickerModal = rememberIvyModal(),
                descriptionModal = rememberIvyModal(),
                trnTimeModal = rememberIvyModal(),
                amountModal = rememberIvyModal(),
            ),
            onEvent = {}
        )
    }
}
// endregion