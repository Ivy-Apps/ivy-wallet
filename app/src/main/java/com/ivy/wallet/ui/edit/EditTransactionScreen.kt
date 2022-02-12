package com.ivy.wallet.ui.edit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.wallet.R
import com.ivy.wallet.base.*
import com.ivy.wallet.logic.model.CreateAccountData
import com.ivy.wallet.logic.model.CreateCategoryData
import com.ivy.wallet.model.TransactionType
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.edit.core.*
import com.ivy.wallet.ui.theme.IvyTheme
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.components.AddPrimaryAttributeButton
import com.ivy.wallet.ui.theme.components.ChangeTransactionTypeModal
import com.ivy.wallet.ui.theme.modal.DeleteModal
import com.ivy.wallet.ui.theme.modal.ModalAdd
import com.ivy.wallet.ui.theme.modal.ModalCheck
import com.ivy.wallet.ui.theme.modal.ModalSave
import com.ivy.wallet.ui.theme.modal.edit.*
import com.ivy.wallet.ui.theme.style
import java.time.LocalDateTime

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.EditTransactionScreen(screen: Screen.EditTransaction) {
    val viewModel: EditTransactionViewModel = viewModel()

    val transactionType by viewModel.transactionType.observeAsState(screen.type)
    val initialTitle by viewModel.initialTitle.observeAsState()
    val titleSuggestions by viewModel.titleSuggestions.collectAsState()
    val currency by viewModel.currency.observeAsState("")
    val description by viewModel.description.observeAsState()
    val dateTime by viewModel.dateTime.observeAsState()
    val category by viewModel.category.observeAsState()
    val account by viewModel.account.observeAsState()
    val toAccount by viewModel.toAccount.observeAsState()
    val dueDate by viewModel.dueDate.observeAsState()
    val amount by viewModel.amount.observeAsState(0.0)
    val isLoanRecord by viewModel.isLoanRecord.observeAsState(false)
    val loanCaption by viewModel.loanCaption.collectAsState()

    val categories by viewModel.categories.observeAsState(emptyList())
    val accounts by viewModel.accounts.observeAsState(emptyList())

    val hasChanges by viewModel.hasChanges.observeAsState(false)

    onScreenStart {
        viewModel.start(screen)
    }

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
        isLoanRecord = isLoanRecord,
        loanCaption = loanCaption,

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
        onSave = viewModel::save,
        onSetHasChanges = viewModel::setHasChanges,
        onDelete = viewModel::delete,
        onCreateAccount = viewModel::createAccount
    )
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: Screen.EditTransaction,
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
    isLoanRecord: Boolean = false,
    loanCaption: String? = null,

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
) {
    var chooseCategoryModalVisible by remember { mutableStateOf(false) }
    var categoryModalData: CategoryModalData? by remember { mutableStateOf(null) }
    var accountModalData: AccountModalData? by remember { mutableStateOf(null) }
    var descriptionModalVisible by remember { mutableStateOf(false) }
    var deleteTrnModalVisible by remember { mutableStateOf(false) }
    var changeTransactionTypeModalVisible by remember { mutableStateOf(false) }
    var amountModalShown by remember { mutableStateOf(false) }

    var titleTextFieldValue by remember(initialTitle) {
        mutableStateOf(
            TextFieldValue(
                initialTitle ?: ""
            )
        )
    }
    val titleFocus = FocusRequester()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
    ) {
        Spacer(Modifier.height(16.dp))

        Toolbar(
            //Setting the transaction type to TransactionType.TRANSFER for transactions associated
            // with loan record to hide the ChangeTransactionType Button
            type = if (isLoanRecord) TransactionType.TRANSFER else transactionType,
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

        if (loanCaption != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = loanCaption,
                style = Typo.numberBody2.style(
                    color = IvyTheme.colors.mediumInverse,
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

        val ivyContext = LocalIvyContext.current

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

        if (dueDate == null && transactionType != TransactionType.TRANSFER && dateTime == null) {
            Spacer(Modifier.height(12.dp))

            AddPrimaryAttributeButton(
                icon = R.drawable.ic_planned_payments,
                text = "Add planned date of payment",
                onClick = {
                    ivyContext.back()
                    ivyContext.navigateTo(
                        Screen.EditPlanned(
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

        Spacer(Modifier.height(600.dp)) //scroll hack
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

        ActionButton = {
            if (screen.initialTransactionId != null) {
                //Edit mode
                if (dueDate != null) {
                    //due date stuff
                    if (hasChanges) {
                        //has changes
                        ModalSave {
                            onSave(false)
                            onSetHasChanges(false)
                        }
                    } else {
                        //no changes, pay
                        ModalCheck(label = if (transactionType == TransactionType.EXPENSE) "Pay" else "Get") {
                            onPayPlannedPayment()
                        }
                    }
                } else {
                    //normal transaction
                    ModalSave {
                        onSave(true)
                    }
                }
            } else {
                //create new mode
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
        onSelectedAccountChanged = onAccountChanged,
        onToAccountChanged = onToAccountChanged,
        onAddNewAccount = {
            accountModalData = AccountModalData(
                account = null,
                baseCurrency = baseCurrency,
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
        title = "Confirm deletion",
        description = "Deleting this transaction will remove it from the transaction history and update the balance accordingly.",
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
    IvyAppPreview {
        UI(
            screen = Screen.EditTransaction(null, TransactionType.EXPENSE),
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