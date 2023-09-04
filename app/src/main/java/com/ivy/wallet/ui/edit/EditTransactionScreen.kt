package com.ivy.wallet.ui.edit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.utils.hideKeyboard
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.R
import com.ivy.wallet.domain.data.CustomExchangeRateState
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.ui.EditPlanned
import com.ivy.wallet.ui.EditTransaction
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.edit.core.Category
import com.ivy.wallet.ui.edit.core.Description
import com.ivy.wallet.ui.edit.core.DueDate
import com.ivy.wallet.ui.edit.core.EditBottomSheet
import com.ivy.wallet.ui.edit.core.Title
import com.ivy.wallet.ui.edit.core.Toolbar
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.ui.loan.data.EditTransactionDisplayLoan
import com.ivy.wallet.ui.rootView
import com.ivy.wallet.ui.theme.components.AddPrimaryAttributeButton
import com.ivy.wallet.ui.theme.components.ChangeTransactionTypeModal
import com.ivy.wallet.ui.theme.components.CustomExchangeRateCard
import com.ivy.wallet.ui.theme.modal.DeleteModal
import com.ivy.wallet.ui.theme.modal.ModalAdd
import com.ivy.wallet.ui.theme.modal.ModalCheck
import com.ivy.wallet.ui.theme.modal.ModalSave
import com.ivy.wallet.ui.theme.modal.ProgressModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModal
import com.ivy.wallet.ui.theme.modal.edit.AccountModalData
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.ui.theme.modal.edit.CategoryModal
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import com.ivy.wallet.ui.theme.modal.edit.ChooseCategoryModal
import com.ivy.wallet.ui.theme.modal.edit.DescriptionModal
import com.ivy.wallet.utils.convertUTCtoLocal
import com.ivy.wallet.utils.getTrueDate
import com.ivy.wallet.utils.onScreenStart
import com.ivy.wallet.utils.timeNowLocal
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.roundToInt

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.EditTransactionScreen(screen: EditTransaction) {
    val viewModel: EditTransactionViewModel = viewModel()

    val transactionType by viewModel.transactionType.observeAsState(screen.type)
    val initialTitle by viewModel.initialTitle.collectAsState()
    val titleSuggestions by viewModel.titleSuggestions.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val description by viewModel.description.collectAsState()
    val dateTime by viewModel.dateTime.collectAsState()
    val category by viewModel.category.collectAsState()
    val account by viewModel.account.collectAsState()
    val toAccount by viewModel.toAccount.collectAsState()
    val dueDate by viewModel.dueDate.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val loanData by viewModel.displayLoanHelper.collectAsState()
    val backgroundProcessing by viewModel.backgroundProcessingStarted.collectAsState()
    val customExchangeRateState by viewModel.customExchangeRateState.collectAsState()

    val categories by viewModel.categories.collectAsState(emptyList())
    val accounts by viewModel.accounts.collectAsState(emptyList())

    val hasChanges by viewModel.hasChanges.collectAsState(false)

    onScreenStart {
        viewModel.start(screen)
    }

    val view = rootView()

    UI(
        screen = screen,
        transactionType = transactionType,
        baseCurrency = currency,
        initialTitle = initialTitle,
        titleSuggestions = titleSuggestions,
        description = description,
        dateTime = dateTime,
        category = category,
        account = account,
        toAccount = toAccount,
        dueDate = dueDate,
        amount = amount,
        loanData = loanData,
        backgroundProcessing = backgroundProcessing,
        customExchangeRateState = customExchangeRateState,

        categories = categories,
        accounts = accounts,

        hasChanges = hasChanges,

        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onAmountChanged = viewModel::onAmountChanged,
        onCategoryChanged = viewModel::onCategoryChanged,
        onAccountChanged = viewModel::onAccountChanged,
        onToAccountChanged = viewModel::onToAccountChanged,
        onDueDateChanged = viewModel::onDueDateChanged,
        onSetDateTime = viewModel::onSetDateTime,
        onSetTransactionType = viewModel::onSetTransactionType,

        onCreateCategory = viewModel::createCategory,
        onEditCategory = viewModel::editCategory,
        onPayPlannedPayment = viewModel::onPayPlannedPayment,
        onSave = {
            view.hideKeyboard()
            viewModel.save()
        },
        onSetHasChanges = viewModel::setHasChanges,
        onDelete = viewModel::delete,
        onCreateAccount = viewModel::createAccount,
        onExchangeRateChanged = {
            viewModel.updateExchangeRate(exRate = it)
        }
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: EditTransaction,
    transactionType: TransactionType,
    baseCurrency: String,
    initialTitle: String?,
    titleSuggestions: Set<String>,
    description: String?,
    category: Category?,
    dateTime: LocalDateTime?,
    account: Account?,
    toAccount: Account?,
    dueDate: LocalDateTime?,
    amount: Double,
    loanData: EditTransactionDisplayLoan = EditTransactionDisplayLoan(),
    backgroundProcessing: Boolean = false,
    customExchangeRateState: CustomExchangeRateState,

    categories: List<Category>,
    accounts: List<Account>,

    hasChanges: Boolean = false,

    onTitleChanged: (String?) -> Unit,
    onDescriptionChanged: (String?) -> Unit,
    onAmountChanged: (Double) -> Unit,
    onCategoryChanged: (Category?) -> Unit,
    onAccountChanged: (Account) -> Unit,
    onToAccountChanged: (Account) -> Unit,
    onDueDateChanged: (LocalDateTime?) -> Unit,
    onSetDateTime: (LocalDateTime) -> Unit,
    onSetTransactionType: (TransactionType) -> Unit,

    onCreateCategory: (CreateCategoryData) -> Unit,
    onEditCategory: (Category) -> Unit,
    onPayPlannedPayment: () -> Unit,
    onSave: (closeScreen: Boolean) -> Unit,
    onSetHasChanges: (hasChanges: Boolean) -> Unit,
    onDelete: () -> Unit,
    onCreateAccount: (CreateAccountData) -> Unit,
    onExchangeRateChanged: (Double?) -> Unit = { }
) {
    var chooseCategoryModalVisible by remember { mutableStateOf(false) }
    var categoryModalData: CategoryModalData? by remember { mutableStateOf(null) }
    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }
    var descriptionModalVisible by remember { mutableStateOf(false) }
    var deleteTrnModalVisible by remember { mutableStateOf(false) }
    var changeTransactionTypeModalVisible by remember { mutableStateOf(false) }
    var amountModalShown by remember { mutableStateOf(false) }
    var exchangeRateAmountModalShown by remember { mutableStateOf(false) }
    var accountChangeModal by remember { mutableStateOf(false) }
    val waitModalVisible by remember(backgroundProcessing) {
        mutableStateOf(backgroundProcessing)
    }
    var selectedAcc by remember(account) {
        mutableStateOf(account)
    }

    val amountModalId =
        remember(screen.initialTransactionId, customExchangeRateState.exchangeRate) {
            UUID.randomUUID()
        }

    var titleTextFieldValue by remember(initialTitle) {
        mutableStateOf(
            TextFieldValue(
                initialTitle ?: ""
            )
        )
    }
    val titleFocus = FocusRequester()
    val scrollState = rememberScrollState()

    // This is to scroll the column to the customExchangeCard composable when it is shown
    var customExchangeRatePosition by remember { mutableStateOf(0F) }
    LaunchedEffect(key1 = customExchangeRateState.showCard) {
        val scrollInt =
            if (customExchangeRateState.showCard) customExchangeRatePosition.roundToInt() else 0
        scrollState.animateScrollTo(scrollInt)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
    ) {
        Spacer(Modifier.height(16.dp))

        Toolbar(
            // Setting the transaction type to TransactionType.TRANSFER for transactions associated
            // with loan record to hide the ChangeTransactionType Button
            type = if (loanData.isLoanRecord) TransactionType.TRANSFER else transactionType,
            initialTransactionId = screen.initialTransactionId,
            onDeleteTrnModal = {
                deleteTrnModalVisible = true
            },
            onChangeTransactionTypeModal = {
                changeTransactionTypeModalVisible = true
            }
        )

        Spacer(Modifier.height(32.dp))

        Title(
            type = transactionType,
            titleFocus = titleFocus,
            initialTransactionId = screen.initialTransactionId,

            titleTextFieldValue = titleTextFieldValue,
            setTitleTextFieldValue = {
                titleTextFieldValue = it
            },
            suggestions = titleSuggestions,
            scrollState = scrollState,

            onTitleChanged = onTitleChanged,
            onNext = {
                when {
                    shouldFocusAmount(amount = amount) -> {
                        amountModalShown = true
                    }
                    else -> {
                        onSave(true)
                    }
                }
            }
        )

        if (loanData.loanCaption != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = loanData.loanCaption,
                style = UI.typo.nB2.style(
                    color = UI.colors.mediumInverse,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        if (transactionType != TransactionType.TRANSFER) {
            Spacer(Modifier.height(32.dp))

            Category(
                category = category,
                onChooseCategory = {
                    chooseCategoryModalVisible = true
                }
            )
        }

        Spacer(Modifier.height(32.dp))

        val ivyContext = ivyWalletCtx()

        if (dueDate != null) {
            DueDate(dueDate = dueDate) {
                ivyContext.datePicker(
                    initialDate = dueDate.toLocalDate()
                ) {
                    onDueDateChanged(it.atTime(12, 0))
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        Description(
            description = description,
            onAddDescription = { descriptionModalVisible = true },
            onEditDescription = { descriptionModalVisible = true }
        )

        TransactionDateTime(
            dateTime = dateTime,
            dueDateTime = dueDate,
        ) {
            ivyContext.datePicker(
                initialDate = dateTime?.convertUTCtoLocal()?.toLocalDate(),
            ) { date ->
                ivyContext.timePicker { time ->
                    onSetDateTime(getTrueDate(date, time))
                }
            }
        }

        if (transactionType == TransactionType.TRANSFER && customExchangeRateState.showCard) {
            Spacer(Modifier.height(12.dp))
            CustomExchangeRateCard(
                fromCurrencyCode = baseCurrency,
                toCurrencyCode = customExchangeRateState.toCurrencyCode ?: baseCurrency,
                exchangeRate = customExchangeRateState.exchangeRate,
                onRefresh = {
                    // Set exchangeRate to null to reset
                    onExchangeRateChanged(null)
                },
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    customExchangeRatePosition = coordinates.positionInParent().y * 0.3f
                }
            ) {
                exchangeRateAmountModalShown = true
            }
        }

        if (dueDate == null && transactionType != TransactionType.TRANSFER && dateTime == null) {
            Spacer(Modifier.height(12.dp))

            val nav = navigation()
            AddPrimaryAttributeButton(
                icon = R.drawable.ic_planned_payments,
                text = stringResource(R.string.add_planned_date_payment),
                onClick = {
                    nav.back()
                    nav.navigateTo(
                        EditPlanned(
                            plannedPaymentRuleId = null,
                            type = transactionType,
                            amount = amount,
                            accountId = account?.id,
                            categoryId = category?.id,
                            title = titleTextFieldValue.text,
                            description = description,
                        )
                    )
                }
            )
        }

        Spacer(Modifier.height(600.dp)) // scroll hack
    }

    onScreenStart {
        if (screen.initialTransactionId == null) {
            amountModalShown = true
        }
    }

    EditBottomSheet(
        initialTransactionId = screen.initialTransactionId,
        type = transactionType,
        accounts = accounts,
        selectedAccount = account,
        toAccount = toAccount,
        amount = amount,
        currency = baseCurrency,
        convertedAmount = customExchangeRateState.convertedAmount,
        convertedAmountCurrencyCode = customExchangeRateState.toCurrencyCode,

        ActionButton = {
            if (screen.initialTransactionId != null) {
                // Edit mode
                if (dueDate != null) {
                    // due date stuff
                    if (hasChanges) {
                        // has changes
                        ModalSave {
                            onSave(false)
                            onSetHasChanges(false)
                        }
                    } else {
                        // no changes, pay
                        ModalCheck(
                            label = if (transactionType == TransactionType.EXPENSE) {
                                stringResource(
                                    R.string.pay
                                )
                            } else {
                                stringResource(R.string.get)
                            }
                        ) {
                            onPayPlannedPayment()
                        }
                    }
                } else {
                    // normal transaction
                    ModalSave {
                        onSave(true)
                    }
                }
            } else {
                // create new mode
                ModalAdd {
                    onSave(true)
                }
            }
        },

        amountModalShown = amountModalShown,
        setAmountModalShown = {
            amountModalShown = it
        },

        onAmountChanged = {
            onAmountChanged(it)
            if (shouldFocusCategory(category, transactionType)) {
                chooseCategoryModalVisible = true
            } else if (shouldFocusTitle(titleTextFieldValue, transactionType)) {
                titleFocus.requestFocus()
            }
        },
        onSelectedAccountChanged = {
            if (loanData.isLoan && account?.currency != it.currency) {
                selectedAcc = it
                accountChangeModal = true
            } else {
                onAccountChanged(it)
            }
        },
        onToAccountChanged = onToAccountChanged,
        onAddNewAccount = {
            accountModalData = AccountModalData(
                account = null,
                baseCurrency = baseCurrency,
                balance = 0.0
            )
        }
    )

    // Modals
    ChooseCategoryModal(
        visible = chooseCategoryModalVisible,
        initialCategory = category,
        categories = categories,
        showCategoryModal = { categoryModalData = CategoryModalData(it) },
        onCategoryChanged = {
            onCategoryChanged(it)
            if (shouldFocusTitle(titleTextFieldValue, transactionType)) {
                titleFocus.requestFocus()
            } else if (shouldFocusAmount(amount = amount)) {
                amountModalShown = true
            }
        },
        dismiss = {
            chooseCategoryModalVisible = false
        }
    )

    CategoryModal(
        modal = categoryModalData,
        onCreateCategory = { createData ->
            onCreateCategory(createData)
            chooseCategoryModalVisible = false
        },
        onEditCategory = onEditCategory,
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
        description = stringResource(R.string.transaction_confirm_deletion_description),
        dismiss = { deleteTrnModalVisible = false }
    ) {
        onDelete()
    }

    ChangeTransactionTypeModal(
        visible = changeTransactionTypeModalVisible,
        includeTransferType = true,
        initialType = transactionType,
        dismiss = {
            changeTransactionTypeModalVisible = false
        }
    ) {
        onSetTransactionType(it)
    }

    DeleteModal(
        visible = accountChangeModal,
        title = stringResource(R.string.confirm_account_change),
        description = stringResource(R.string.confirm_account_change_description),
        buttonText = stringResource(R.string.confirm),
        iconStart = R.drawable.ic_agreed,
        dismiss = {
            accountChangeModal = false
        }
    ) {
        selectedAcc?.let { onAccountChanged(it) }
        accountChangeModal = false
    }

    ProgressModal(
        title = stringResource(R.string.confirm_account_change),
        description = stringResource(R.string.account_change_recalculating),
        visible = waitModalVisible
    )

    AmountModal(
        id = amountModalId,
        visible = exchangeRateAmountModalShown,
        currency = "",
        initialAmount = customExchangeRateState.exchangeRate,
        dismiss = { exchangeRateAmountModalShown = false },
        decimalCountMax = 4,
        onAmountChanged = {
            onExchangeRateChanged(it)
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

private fun shouldFocusAmount(amount: Double) = amount == 0.0

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            screen = EditTransaction(null, TransactionType.EXPENSE),
            initialTitle = "",
            titleSuggestions = emptySet(),
            baseCurrency = "BGN",
            dateTime = timeNowLocal(),
            description = null,
            category = null,
            account = Account(name = "phyre"),
            toAccount = null,
            amount = 0.0,
            dueDate = null,
            transactionType = TransactionType.INCOME,
            customExchangeRateState = CustomExchangeRateState(),

            categories = emptyList(),
            accounts = emptyList(),

            onDueDateChanged = {},
            onCategoryChanged = {},
            onAccountChanged = {},
            onToAccountChanged = {},
            onDescriptionChanged = {},
            onTitleChanged = {},
            onAmountChanged = {},

            onCreateCategory = { },
            onEditCategory = {},
            onPayPlannedPayment = {},
            onSave = {},
            onSetHasChanges = {},
            onDelete = {},
            onCreateAccount = { },
            onSetDateTime = {},
            onSetTransactionType = {}
        )
    }
}
