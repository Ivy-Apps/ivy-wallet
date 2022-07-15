package com.ivy.wallet.ui.planned.edit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.base.IvyWalletPreview
import com.ivy.base.R
import com.ivy.data.Account
import com.ivy.data.Category
import com.ivy.data.planned.IntervalType
import com.ivy.data.transaction.TransactionType
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.screens.EditPlanned
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.ui.edit.core.*
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.components.ChangeTransactionTypeModal
import com.ivy.wallet.ui.theme.modal.DeleteModal
import com.ivy.wallet.ui.theme.modal.ModalSet
import com.ivy.wallet.ui.theme.modal.RecurringRuleModal
import com.ivy.wallet.ui.theme.modal.RecurringRuleModalData
import com.ivy.wallet.ui.theme.modal.edit.*
import java.time.LocalDateTime

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.EditPlannedScreen(screen: EditPlanned) {
    val viewModel: EditPlannedViewModel = viewModel()

    val startDate by viewModel.startDate.observeAsState()
    val intervalN by viewModel.intervalN.observeAsState()
    val intervalType by viewModel.intervalType.observeAsState()
    val oneTime by viewModel.oneTime.observeAsState(false)

    val transactionType by viewModel.transactionType.observeAsState(screen.type)
    val initialTitle by viewModel.initialTitle.observeAsState()
    val currency by viewModel.currency.observeAsState("")
    val description by viewModel.description.observeAsState()
    val category by viewModel.category.observeAsState()
    val account by viewModel.account.observeAsState()
    val amount by viewModel.amount.observeAsState(0.0)

    val categories by viewModel.categories.observeAsState(emptyList())
    val accounts by viewModel.accounts.observeAsState(emptyList())

    onScreenStart {
        viewModel.start(screen)
    }

    UI(
        screen = screen,
        startDate = startDate,
        intervalN = intervalN,
        intervalType = intervalType,
        oneTime = oneTime,
        type = transactionType,
        currency = currency,
        initialTitle = initialTitle,
        description = description,
        category = category,
        account = account,
        amount = amount,

        categories = categories,
        accounts = accounts,

        onRuleChanged = viewModel::onRuleChanged,
        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onAmountChanged = viewModel::onAmountChanged,
        onCategoryChanged = viewModel::onCategoryChanged,
        onAccountChanged = viewModel::onAccountChanged,
        onSetTransactionType = viewModel::onSetTransactionType,

        onCreateCategory = viewModel::createCategory,
        onSave = viewModel::save,
        onDelete = viewModel::delete,
        onCreateAccount = viewModel::createAccount
    )
}

/**
 * Flow Empty: Type -> Amount -> Category -> Recurring Rule -> Title
 * Flow Amount + Category: Recurring Rule -> Title
 */

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: EditPlanned,

    startDate: LocalDateTime?,
    intervalN: Int?,
    intervalType: IntervalType?,
    oneTime: Boolean,

    type: TransactionType,
    currency: String,
    initialTitle: String?,
    description: String?,
    category: Category?,
    account: Account?,
    amount: Double,

    categories: List<Category>,
    accounts: List<Account>,

    onRuleChanged: (LocalDateTime, oneTime: Boolean, Int?, IntervalType?) -> Unit,
    onTitleChanged: (String?) -> Unit,
    onDescriptionChanged: (String?) -> Unit,
    onAmountChanged: (Double) -> Unit,
    onCategoryChanged: (Category?) -> Unit,
    onAccountChanged: (Account) -> Unit,
    onSetTransactionType: (TransactionType) -> Unit,

    onCreateCategory: (CreateCategoryData) -> Unit = {},
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onCreateAccount: (CreateAccountData) -> Unit = {},
) {
    var chooseCategoryModalVisible by remember { mutableStateOf(false) }
    var categoryModalData: CategoryModalData? by remember { mutableStateOf(null) }
    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }
    var descriptionModalVisible by remember { mutableStateOf(false) }
    var deleteTrnModalVisible by remember { mutableStateOf(false) }
    var changeTransactionTypeModalVisible by remember { mutableStateOf(false) }
    var amountModalShown by remember { mutableStateOf(false) }
    var recurringRuleModal: RecurringRuleModalData? by remember { mutableStateOf(null) }

    var titleTextFieldValue by remember(initialTitle) {
        mutableStateOf(
            TextFieldValue(
                initialTitle ?: ""
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
            type = type,
            initialTransactionId = screen.plannedPaymentRuleId,
            onDeleteTrnModal = {
                deleteTrnModalVisible = true
            },
            onChangeTransactionTypeModal = {
                changeTransactionTypeModalVisible = true
            }
        )

        Spacer(Modifier.height(32.dp))

        Title(
            type = type,
            titleFocus = titleFocus,
            initialTransactionId = screen.plannedPaymentRuleId,

            titleTextFieldValue = titleTextFieldValue,
            setTitleTextFieldValue = {
                titleTextFieldValue = it
            },
            suggestions = emptySet(), //DO NOT display title suggestions for "Planned Payments"

            onTitleChanged = onTitleChanged,
            onNext = {
                when {
                    shouldFocusRecurring(startDate, intervalN, intervalType, oneTime) -> {
                        recurringRuleModal = RecurringRuleModalData(
                            initialStartDate = startDate,
                            initialIntervalN = intervalN,
                            initialIntervalType = intervalType,
                            initialOneTime = oneTime
                        )
                    }
                    else -> {
                        onSave()
                    }
                }
            }
        )

        if (type != TransactionType.TRANSFER) {
            Spacer(Modifier.height(32.dp))

            Category(
                category = category,
                onChooseCategory = {
                    chooseCategoryModalVisible = true
                }
            )
        }

        Spacer(Modifier.height(32.dp))

        RecurringRule(
            startDate = startDate,
            intervalN = intervalN,
            intervalType = intervalType,
            oneTime = oneTime,
            onShowRecurringRuleModal = {
                recurringRuleModal = RecurringRuleModalData(
                    initialStartDate = startDate,
                    initialIntervalN = intervalN,
                    initialIntervalType = intervalType,
                    initialOneTime = oneTime
                )
            }
        )

        Spacer(Modifier.height(12.dp))

        Description(
            description = description,
            onAddDescription = { descriptionModalVisible = true },
            onEditDescription = { descriptionModalVisible = true }
        )

        Spacer(Modifier.height(600.dp))//scroll hack
    }

    onScreenStart {
        if (screen.plannedPaymentRuleId == null) {
            //Create mode
            if (screen.mandatoryFilled()) {
                //Flow Convert (Amount, Account, Category)
                recurringRuleModal = RecurringRuleModalData(
                    initialStartDate = startDate,
                    initialIntervalN = intervalN,
                    initialIntervalType = intervalType,
                    initialOneTime = oneTime
                )
            } else {
                //Flow Empty
                changeTransactionTypeModalVisible = true
            }
        }
    }

    EditBottomSheet(
        initialTransactionId = screen.plannedPaymentRuleId,
        type = type,
        accounts = accounts,
        selectedAccount = account,
        toAccount = null,
        amount = amount,
        currency = currency,

        ActionButton = {
            ModalSet(
                modifier = Modifier.testTag("editPlannedScreen_set")
            ) {
                onSave()
            }
        },

        amountModalShown = amountModalShown,
        setAmountModalShown = {
            amountModalShown = it
        },

        onAmountChanged = {
            onAmountChanged(it)
            when {
                shouldFocusCategory(category, type) -> {
                    chooseCategoryModalVisible = true
                }
                shouldFocusRecurring(startDate, intervalN, intervalType, oneTime) -> {
                    recurringRuleModal = RecurringRuleModalData(
                        initialStartDate = startDate,
                        initialIntervalN = intervalN,
                        initialIntervalType = intervalType,
                        initialOneTime = oneTime
                    )
                }
                shouldFocusTitle(titleTextFieldValue, type) -> {
                    titleFocus.requestFocus()
                }
            }
        },
        onSelectedAccountChanged = onAccountChanged,
        onToAccountChanged = { },
        onAddNewAccount = {
            accountModalData = AccountModalData(
                account = null,
                baseCurrency = currency,
                balance = 0.0
            )
        }
    )

    //Modals
    ChooseCategoryModal(
        visible = chooseCategoryModalVisible,
        initialCategory = category,
        categories = categories,
        showCategoryModal = { categoryModalData = CategoryModalData(it) },
        onCategoryChanged = {
            onCategoryChanged(it)

            recurringRuleModal = RecurringRuleModalData(
                initialStartDate = startDate,
                initialIntervalN = intervalN,
                initialIntervalType = intervalType,
                initialOneTime = oneTime
            )
        },
        dismiss = {
            chooseCategoryModalVisible = false
        }
    )

    CategoryModal(
        modal = categoryModalData,
        onCreateCategory = onCreateCategory,
        onEditCategory = { },
        dismiss = {
            categoryModalData = null
        }
    )

    AccountModal(
        modal = accountModalData,
        onCreateAccount = onCreateAccount,
        onEditAccount = { _, _ -> },
        dismiss = {
            accountModalData = null
        }
    )

    DescriptionModal(
        visible = descriptionModalVisible,
        description = description,
        onDescriptionChanged = onDescriptionChanged,
        dismiss = {
            descriptionModalVisible = false
        }
    )

    DeleteModal(
        visible = deleteTrnModalVisible,
        title = stringResource(R.string.confirm_deletion),
        description = stringResource(R.string.planned_payment_confirm_deletion_description),
        dismiss = { deleteTrnModalVisible = false }
    ) {
        onDelete()
    }

    ChangeTransactionTypeModal(
        title = stringResource(R.string.set_payment_type),
        visible = changeTransactionTypeModalVisible,
        includeTransferType = false,
        initialType = type,
        dismiss = {
            changeTransactionTypeModalVisible = false
        }
    ) {
        onSetTransactionType(it)
        if (shouldFocusAmount(amount)) {
            amountModalShown = true
        }
    }

    RecurringRuleModal(
        modal = recurringRuleModal,
        onRuleChanged = { newStartDate, newOneTime, newIntervalN, newIntervalType ->
            onRuleChanged(newStartDate, newOneTime, newIntervalN, newIntervalType)

            when {
                shouldFocusCategory(category, type) -> {
                    chooseCategoryModalVisible = true
                }
                shouldFocusTitle(titleTextFieldValue, type) -> {
                    titleFocus.requestFocus()
                }
            }
        },
        dismiss = {
            recurringRuleModal = null
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
            screen = EditPlanned(null, TransactionType.EXPENSE),
            oneTime = false,
            startDate = null,
            intervalN = null,
            intervalType = null,
            initialTitle = "",
            currency = "BGN",
            description = null,
            category = null,
            account = Account(name = "phyre", color = Green.toArgb()),
            amount = 0.0,
            type = TransactionType.INCOME,

            categories = emptyList(),
            accounts = emptyList(),

            onRuleChanged = { _, _, _, _ -> },
            onCategoryChanged = {},
            onAccountChanged = {},
            onDescriptionChanged = {},
            onTitleChanged = {},
            onAmountChanged = {},

            onCreateCategory = { },
            onSave = {},
            onDelete = {},
            onCreateAccount = { },
            onSetTransactionType = {}
        )
    }
}