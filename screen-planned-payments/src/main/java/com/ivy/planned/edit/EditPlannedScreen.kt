package com.ivy.planned.edit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.model.TransactionType
import com.ivy.data.model.IntervalType
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.utils.onScreenStart
import com.ivy.navigation.EditPlannedScreen
import com.ivy.navigation.screenScopedViewModel
import com.ivy.resources.R
import com.ivy.wallet.ui.edit.core.Category
import com.ivy.wallet.ui.edit.core.Description
import com.ivy.wallet.ui.edit.core.EditBottomSheet
import com.ivy.wallet.ui.edit.core.Title
import com.ivy.wallet.ui.edit.core.Toolbar
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.components.ChangeTransactionTypeModal
import com.ivy.wallet.ui.theme.modal.DeleteModal
import com.ivy.wallet.ui.theme.modal.ModalSet
import com.ivy.wallet.ui.theme.modal.RecurringRuleModal
import com.ivy.wallet.ui.theme.modal.RecurringRuleModalData
import com.ivy.wallet.ui.theme.modal.edit.AccountModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData
import com.ivy.wallet.ui.theme.modal.edit.CategoryModal
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import com.ivy.wallet.ui.theme.modal.edit.ChooseCategoryModal
import com.ivy.wallet.ui.theme.modal.edit.DescriptionModal
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.EditPlannedScreen(screen: EditPlannedScreen) {
    val viewModel: EditPlannedViewModel = screenScopedViewModel()
    val uiState = viewModel.uiState()
    LaunchedEffect(Unit) {
        viewModel.start(screen)
    }

    UI(
        screen = screen,
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

/**
 * Flow Empty: Type -> Amount -> Category -> Recurring Rule -> Title
 * Flow Amount + Category: Recurring Rule -> Title
 */

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: EditPlannedScreen,
    state: EditPlannedScreenState,
    onEvent: (EditPlannedScreenEvent) -> Unit
) {
    var titleTextFieldValue by remember(state.initialTitle) {
        mutableStateOf(
            TextFieldValue(
                state.initialTitle.orEmpty()
            )
        )
    }
    val titleFocus = FocusRequester()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))

        Toolbar(
            type = state.transactionType,
            initialTransactionId = screen.plannedPaymentRuleId,
            onDeleteTrnModal = {
                onEvent(EditPlannedScreenEvent.OnDeleteTransactionModalVisible(true))
            },
            onChangeTransactionTypeModal = {
                onEvent(EditPlannedScreenEvent.OnTransactionTypeModalVisible(true))
            }
        )

        Spacer(Modifier.height(32.dp))

        Title(
            type = state.transactionType,
            titleFocus = titleFocus,
            initialTransactionId = screen.plannedPaymentRuleId,

            titleTextFieldValue = titleTextFieldValue,
            setTitleTextFieldValue = {
                titleTextFieldValue = it
            },
            suggestions = emptySet(), // DO NOT display title suggestions for "Planned Payments"

            onTitleChanged = { onEvent(EditPlannedScreenEvent.OnTitleChanged(it)) },
            onNext = {
                when {
                    shouldFocusRecurring(
                        state.startDate,
                        state.intervalN,
                        state.intervalType,
                        state.oneTime
                    ) -> {
                        onEvent(
                            EditPlannedScreenEvent.OnRecurringRuleModalDataChanged(
                                RecurringRuleModalData(
                                    initialStartDate = state.startDate,
                                    initialIntervalN = state.intervalN,
                                    initialIntervalType = state.intervalType,
                                    initialOneTime = state.oneTime
                                )
                            )
                        )
                    }

                    else -> {
                        onEvent(EditPlannedScreenEvent.OnSave())
                    }
                }
            }
        )

        if (state.transactionType != TransactionType.TRANSFER) {
            Spacer(Modifier.height(32.dp))

            Category(
                category = state.category,
                onChooseCategory = {
                    onEvent(EditPlannedScreenEvent.OnCategoryModalVisible(true))
                }
            )
        }

        Spacer(Modifier.height(32.dp))

        RecurringRule(
            startDate = state.startDate,
            intervalN = state.intervalN,
            intervalType = state.intervalType,
            oneTime = state.oneTime,
            onShowRecurringRuleModal = {
                onEvent(
                    EditPlannedScreenEvent.OnRecurringRuleModalDataChanged(
                        RecurringRuleModalData(
                            initialStartDate = state.startDate,
                            initialIntervalN = state.intervalN,
                            initialIntervalType = state.intervalType,
                            initialOneTime = state.oneTime
                        )
                    )
                )
            }
        )

        Spacer(Modifier.height(12.dp))

        Description(
            description = state.description,
            onAddDescription = { onEvent(EditPlannedScreenEvent.OnDescriptionModalVisible(true)) },
            onEditDescription = { onEvent(EditPlannedScreenEvent.OnDescriptionModalVisible(true)) }
        )

        Spacer(Modifier.height(600.dp)) // scroll hack
    }

    onScreenStart {
        if (screen.plannedPaymentRuleId == null) {
            // Create mode
            if (screen.mandatoryFilled()) {
                // Flow Convert (Amount, Account, Category)
                onEvent(
                    EditPlannedScreenEvent.OnRecurringRuleModalDataChanged(
                        RecurringRuleModalData(
                            initialStartDate = state.startDate,
                            initialIntervalN = state.intervalN,
                            initialIntervalType = state.intervalType,
                            initialOneTime = state.oneTime
                        )
                    )
                )
            } else {
                // Flow Empty
                onEvent(EditPlannedScreenEvent.OnTransactionTypeModalVisible(true))
            }
        }
    }

    EditBottomSheet(
        initialTransactionId = screen.plannedPaymentRuleId,
        type = state.transactionType,
        accounts = state.accounts,
        selectedAccount = state.account,
        toAccount = null,
        amount = state.amount,
        currency = state.currency,

        ActionButton = {
            ModalSet(
                modifier = Modifier.testTag("editPlannedScreen_set")
            ) {
                onEvent(EditPlannedScreenEvent.OnSave())
            }
        },

        amountModalShown = state.amountModalVisible,
        setAmountModalShown = {
            onEvent(EditPlannedScreenEvent.OnAmountModalVisible(it))
        },

        onAmountChanged = {
            onEvent(EditPlannedScreenEvent.OnAmountChanged(it))
            when {
                shouldFocusCategory(state.category, state.transactionType) -> {
                    onEvent(EditPlannedScreenEvent.OnCategoryModalVisible(true))
                }

                shouldFocusRecurring(
                    state.startDate,
                    state.intervalN,
                    state.intervalType,
                    state.oneTime
                ) -> {
                    onEvent(
                        EditPlannedScreenEvent.OnRecurringRuleModalDataChanged(
                            RecurringRuleModalData(
                                initialStartDate = state.startDate,
                                initialIntervalN = state.intervalN,
                                initialIntervalType = state.intervalType,
                                initialOneTime = state.oneTime
                            )
                        )
                    )
                }

                shouldFocusTitle(titleTextFieldValue, state.transactionType) -> {
                    titleFocus.requestFocus()
                }
            }
        },
        onSelectedAccountChanged = { onEvent(EditPlannedScreenEvent.OnAccountChanged(it)) },
        onToAccountChanged = { },
        onAddNewAccount = {
            onEvent(
                EditPlannedScreenEvent.OnAccountModalDataChanged(
                    AccountModalData(
                        account = null,
                        baseCurrency = state.currency,
                        balance = 0.0
                    )
                )
            )
        }
    )

    // Modals
    ChooseCategoryModal(
        visible = state.categoryModalVisible,
        initialCategory = state.category,
        categories = state.categories,
        showCategoryModal = {
            onEvent(
                EditPlannedScreenEvent.OnCategoryModalDataChanged(
                    CategoryModalData(it)
                )
            )
        },
        onCategoryChanged = {
            onEvent(EditPlannedScreenEvent.OnCategoryChanged(it))
            onEvent(
                EditPlannedScreenEvent.OnRecurringRuleModalDataChanged(
                    RecurringRuleModalData(
                        initialStartDate = state.startDate,
                        initialIntervalN = state.intervalN,
                        initialIntervalType = state.intervalType,
                        initialOneTime = state.oneTime
                    )
                )
            )
        },
        dismiss = {
            onEvent(EditPlannedScreenEvent.OnCategoryModalVisible(false))
        }
    )

    CategoryModal(
        modal = state.categoryModalData,
        onCreateCategory = { onEvent(EditPlannedScreenEvent.OnCreateCategory(it)) },
        onEditCategory = { },
        dismiss = {
            onEvent(EditPlannedScreenEvent.OnCategoryModalDataChanged(null))
        }
    )

    AccountModal(
        modal = state.accountModalData,
        onCreateAccount = { onEvent(EditPlannedScreenEvent.OnCreateAccount(it)) },
        onEditAccount = { _, _ -> },
        dismiss = {
            onEvent(EditPlannedScreenEvent.OnAccountModalDataChanged(null))
        }
    )

    DescriptionModal(
        visible = state.descriptionModalVisible,
        description = state.description,
        onDescriptionChanged = { onEvent(EditPlannedScreenEvent.OnDescriptionChanged(it)) },
        dismiss = {
            onEvent(EditPlannedScreenEvent.OnDescriptionModalVisible(false))
        }
    )

    DeleteModal(
        visible = state.deleteTransactionModalVisible,
        title = stringResource(R.string.confirm_deletion),
        description = stringResource(R.string.planned_payment_confirm_deletion_description),
        dismiss = { onEvent(EditPlannedScreenEvent.OnDeleteTransactionModalVisible(false)) }
    ) {
        onEvent(EditPlannedScreenEvent.OnDelete)
    }

    ChangeTransactionTypeModal(
        title = stringResource(R.string.set_payment_type),
        visible = state.transactionTypeModalVisible,
        includeTransferType = false,
        initialType = state.transactionType,
        dismiss = {
            onEvent(EditPlannedScreenEvent.OnTransactionTypeModalVisible(false))
        }
    ) {
        onEvent(EditPlannedScreenEvent.OnSetTransactionType(it))
        if (shouldFocusAmount(state.amount)) {
            onEvent(EditPlannedScreenEvent.OnAmountModalVisible(true))
        }
    }

    RecurringRuleModal(
        modal = state.recurringRuleModalData,
        onRuleChanged = { newStartDate, newOneTime, newIntervalN, newIntervalType ->
            onEvent(
                EditPlannedScreenEvent.OnRuleChanged(
                    newStartDate,
                    newOneTime,
                    newIntervalN,
                    newIntervalType
                )
            )

            when {
                shouldFocusCategory(state.category, state.transactionType) -> {
                    onEvent(EditPlannedScreenEvent.OnCategoryModalVisible(true))
                }

                shouldFocusTitle(titleTextFieldValue, state.transactionType) -> {
                    titleFocus.requestFocus()
                }
            }
        },
        dismiss = {
            onEvent(EditPlannedScreenEvent.OnRecurringRuleModalDataChanged(null))
        }
    )
}

private fun shouldFocusCategory(
    category: Category?,
    type: TransactionType
): Boolean = category == null && type != TransactionType.TRANSFER

private fun shouldFocusTitle(
    titleTextFieldValue: TextFieldValue,
    type: TransactionType
): Boolean = titleTextFieldValue.text.isBlank() && type != TransactionType.TRANSFER

private fun shouldFocusRecurring(
    startDate: LocalDateTime?,
    intervalN: Int?,
    intervalType: IntervalType?,
    oneTime: Boolean,
): Boolean {
    return !hasRecurringRule(
        startDate = startDate,
        intervalN = intervalN,
        intervalType = intervalType,
        oneTime = oneTime
    )
}

private fun shouldFocusAmount(amount: Double) = amount == 0.0

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            screen = EditPlannedScreen(null, TransactionType.EXPENSE),
            EditPlannedScreenState(
                oneTime = false,
                startDate = null,
                intervalN = null,
                intervalType = null,
                initialTitle = "",
                currency = "BGN",
                description = null,
                category = null,
                account = Account(name = "phyre", Orange.toArgb()),
                amount = 0.0,
                transactionType = TransactionType.INCOME,
                categories = persistentListOf(),
                accounts = persistentListOf(),
                categoryModalVisible = false,
                categoryModalData = null,
                accountModalData = null,
                descriptionModalVisible = false,
                deleteTransactionModalVisible = false,
                recurringRuleModalData = null,
                transactionTypeModalVisible = false,
                amountModalVisible = false
            )
        ) {}
    }
}
