package com.ivy.transaction

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.model.TransactionType
import com.ivy.design.l0_system.Orange
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.utils.hideKeyboard
import com.ivy.legacy.data.EditTransactionDisplayLoan
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.ivyWalletCtx
import com.ivy.legacy.rootView
import com.ivy.legacy.ui.component.edit.TransactionDateTime
import com.ivy.legacy.utils.convertUTCtoLocal
import com.ivy.legacy.utils.onScreenStart
import com.ivy.legacy.utils.timeNowLocal
import com.ivy.navigation.EditPlannedScreen
import com.ivy.navigation.EditTransactionScreen
import com.ivy.navigation.IvyPreview
import com.ivy.navigation.navigation
import com.ivy.navigation.screenScopedViewModel
import com.ivy.resources.R
import com.ivy.wallet.domain.data.CustomExchangeRateState
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.ui.edit.core.Category
import com.ivy.wallet.ui.edit.core.Description
import com.ivy.wallet.ui.edit.core.DueDate
import com.ivy.wallet.ui.edit.core.EditBottomSheet
import com.ivy.wallet.ui.edit.core.Title
import com.ivy.wallet.ui.edit.core.Toolbar
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
import java.time.LocalDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import kotlin.math.roundToInt

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.EditTransactionScreen(screen: EditTransactionScreen) {
    val viewModel: EditTransactionViewModel = screenScopedViewModel()
    val uiState = viewModel.uiState()

    LaunchedEffect(Unit) {
        viewModel.start(screen)
    }

    val view = rootView()

    UI(screen = screen,
        transactionType = uiState.transactionType,
        baseCurrency = uiState.currency,
        initialTitle = uiState.initialTitle,
        titleSuggestions = uiState.titleSuggestions,
        description = uiState.description,
        dateTime = uiState.dateTime,
        category = uiState.category,
        account = uiState.account,
        toAccount = uiState.toAccount,
        dueDate = uiState.dueDate,
        amount = uiState.amount,
        loanData = uiState.displayLoanHelper,
        backgroundProcessing = uiState.backgroundProcessingStarted,
        customExchangeRateState = uiState.customExchangeRateState,

        categories = uiState.categories,
        accounts = uiState.accounts,

        hasChanges = uiState.hasChanges,
        onSetDate = {
            viewModel.onEvent(EditTransactionEvent.OnSetDate(it))
        },
        onSetTime = {
            viewModel.onEvent(EditTransactionEvent.OnSetTime(it))
        },
        onTitleChanged = {
            viewModel.onEvent(EditTransactionEvent.OnTitleChanged(it))
        },
        onDescriptionChanged = {
            viewModel.onEvent(EditTransactionEvent.OnDescriptionChanged(it))
        },
        onAmountChanged = {
            viewModel.onEvent(EditTransactionEvent.OnAmountChanged(it))
        },
        onCategoryChanged = {
            viewModel.onEvent(EditTransactionEvent.OnCategoryChanged(it))
        },
        onAccountChanged = {
            viewModel.onEvent(EditTransactionEvent.OnAccountChanged(it))
        },
        onToAccountChanged = {
            viewModel.onEvent(EditTransactionEvent.OnToAccountChanged(it))
        },
        onDueDateChanged = {
            viewModel.onEvent(EditTransactionEvent.OnDueDateChanged(it))
        },
        onSetTransactionType = {
            viewModel.onEvent(EditTransactionEvent.OnSetTransactionType(it))
        },
        onCreateCategory = {
            viewModel.onEvent(EditTransactionEvent.CreateCategory(it))
        },
        onEditCategory = {
            viewModel.onEvent(EditTransactionEvent.EditCategory(it))
        },
        onPayPlannedPayment = {
            viewModel.onEvent(EditTransactionEvent.OnPayPlannedPayment)
        },
        onSave = {
            view.hideKeyboard()
            viewModel.onEvent(EditTransactionEvent.Save(it))
        },
        onSetHasChanges = {
            viewModel.onEvent(EditTransactionEvent.SetHasChanges(it))
        },
        onDelete = {
            viewModel.onEvent(EditTransactionEvent.Delete)
        },
        onCreateAccount = {
            viewModel.onEvent(EditTransactionEvent.CreateAccount(it))
        },
        onExchangeRateChanged = {
            viewModel.onEvent(EditTransactionEvent.UpdateExchangeRate(it))
        })
}

@ExperimentalFoundationApi
@Composable
private fun BoxWithConstraintsScope.UI(
    screen: EditTransactionScreen,
    transactionType: TransactionType,
    baseCurrency: String,
    initialTitle: String?,
    titleSuggestions: ImmutableSet<String>,
    description: String?,
    category: Category?,
    dateTime: LocalDateTime?,
    account: Account?,
    toAccount: Account?,
    dueDate: LocalDateTime?,
    amount: Double,

    customExchangeRateState: CustomExchangeRateState,
    categories: ImmutableList<Category>,
    accounts: ImmutableList<Account>,
    onTitleChanged: (String?) -> Unit,
    onDescriptionChanged: (String?) -> Unit,
    onAmountChanged: (Double) -> Unit,
    onCategoryChanged: (Category?) -> Unit,
    onAccountChanged: (Account) -> Unit,
    onToAccountChanged: (Account) -> Unit,
    onDueDateChanged: (LocalDateTime?) -> Unit,
    onSetDate: (LocalDate) -> Unit,
    onSetTime: (LocalTime) -> Unit,
    onSetTransactionType: (TransactionType) -> Unit,

    onCreateCategory: (CreateCategoryData) -> Unit,
    onEditCategory: (Category) -> Unit,
    onPayPlannedPayment: () -> Unit,
    onSave: (closeScreen: Boolean) -> Unit,
    onSetHasChanges: (hasChanges: Boolean) -> Unit,
    onDelete: () -> Unit,
    onCreateAccount: (CreateAccountData) -> Unit,
    onExchangeRateChanged: (Double?) -> Unit = { },
    loanData: EditTransactionDisplayLoan = EditTransactionDisplayLoan(),
    backgroundProcessing: Boolean = false,
    hasChanges: Boolean = false,

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
            })

        Spacer(Modifier.height(32.dp))

        Title(type = transactionType,
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
            })

        if (loanData.loanCaption != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = loanData.loanCaption!!,
                style = UI.typo.nB2.style(
                    color = UI.colors.mediumInverse, fontWeight = FontWeight.Normal
                )
            )
        }

        if (transactionType != TransactionType.TRANSFER) {
            Spacer(Modifier.height(32.dp))

            Category(category = category, onChooseCategory = {
                chooseCategoryModalVisible = true
            })
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

        Description(description = description,
            onAddDescription = { descriptionModalVisible = true },
            onEditDescription = { descriptionModalVisible = true })

        TransactionDateTime(dateTime = dateTime, dueDateTime = dueDate, onEditDate = {
            ivyContext.datePicker(
                initialDate = dateTime?.convertUTCtoLocal()?.toLocalDate()
            ) { date ->
                onSetDate((date))
            }
        }, onEditTime = {
            ivyContext.timePicker { time ->
                onSetTime(time)
            }
        })

        if (transactionType == TransactionType.TRANSFER && customExchangeRateState.showCard) {
            Spacer(Modifier.height(12.dp))
            CustomExchangeRateCard(fromCurrencyCode = baseCurrency,
                toCurrencyCode = customExchangeRateState.toCurrencyCode ?: baseCurrency,
                exchangeRate = customExchangeRateState.exchangeRate,
                onRefresh = {
                    // Set exchangeRate to null to reset
                    onExchangeRateChanged(null)
                },
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    customExchangeRatePosition = coordinates.positionInParent().y * 0.3f
                }) {
                exchangeRateAmountModalShown = true
            }
        }

        if (dueDate == null && transactionType != TransactionType.TRANSFER && dateTime == null) {
            Spacer(Modifier.height(12.dp))

            val nav = navigation()
            AddPrimaryAttributeButton(icon = R.drawable.ic_planned_payments,
                text = stringResource(R.string.add_planned_date_payment),
                onClick = {
                    nav.back()
                    nav.navigateTo(
                        EditPlannedScreen(
                            plannedPaymentRuleId = null,
                            type = transactionType,
                            amount = amount,
                            accountId = account?.id,
                            categoryId = category?.id,
                            title = titleTextFieldValue.text,
                            description = description,
                        )
                    )
                })
        }

        Spacer(Modifier.height(600.dp)) // scroll hack
    }

    onScreenStart {
        if (screen.initialTransactionId == null) {
            amountModalShown = true
        }
    }

    EditBottomSheet(initialTransactionId = screen.initialTransactionId,
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
                account = null, baseCurrency = baseCurrency, balance = 0.0
            )
        })

    // Modals
    ChooseCategoryModal(visible = chooseCategoryModalVisible,
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
        })

    CategoryModal(modal = categoryModalData, onCreateCategory = { createData ->
        onCreateCategory(createData)
        chooseCategoryModalVisible = false
    }, onEditCategory = onEditCategory, dismiss = {
        categoryModalData = null
    })

    AccountModal(modal = accountModalData,
        onCreateAccount = onCreateAccount,
        onEditAccount = { _, _ -> },
        dismiss = {
            accountModalData = null
        })

    DescriptionModal(visible = descriptionModalVisible,
        description = description,
        onDescriptionChanged = onDescriptionChanged,
        dismiss = {
            descriptionModalVisible = false
        })

    DeleteModal(visible = deleteTrnModalVisible,
        title = stringResource(R.string.confirm_deletion),
        description = stringResource(R.string.transaction_confirm_deletion_description),
        dismiss = { deleteTrnModalVisible = false }) {
        onDelete()
    }

    ChangeTransactionTypeModal(visible = changeTransactionTypeModalVisible,
        includeTransferType = true,
        initialType = transactionType,
        dismiss = {
            changeTransactionTypeModalVisible = false
        }) {
        onSetTransactionType(it)
    }

    DeleteModal(visible = accountChangeModal,
        title = stringResource(R.string.confirm_account_change),
        description = stringResource(R.string.confirm_account_change_description),
        buttonText = stringResource(R.string.confirm),
        iconStart = R.drawable.ic_agreed,
        dismiss = {
            accountChangeModal = false
        }) {
        selectedAcc?.let { onAccountChanged(it) }
        accountChangeModal = false
    }

    ProgressModal(
        title = stringResource(R.string.confirm_account_change),
        description = stringResource(R.string.account_change_recalculating),
        visible = waitModalVisible
    )

    AmountModal(id = amountModalId,
        visible = exchangeRateAmountModalShown,
        currency = "",
        initialAmount = customExchangeRateState.exchangeRate,
        dismiss = { exchangeRateAmountModalShown = false },
        decimalCountMax = 4,
        onAmountChanged = {
            onExchangeRateChanged(it)
        })
}

private fun shouldFocusCategory(
    category: Category?, type: TransactionType
): Boolean = category == null && type != TransactionType.TRANSFER

private fun shouldFocusTitle(
    titleTextFieldValue: TextFieldValue, type: TransactionType
): Boolean = titleTextFieldValue.text.isBlank() && type != TransactionType.TRANSFER

private fun shouldFocusAmount(amount: Double) = amount == 0.0

@ExperimentalFoundationApi
@Preview
@Composable
private fun BoxWithConstraintsScope.Preview() {
    IvyPreview {
        UI(screen = EditTransactionScreen(null, TransactionType.EXPENSE),
            initialTitle = "",
            titleSuggestions = persistentSetOf(),
            baseCurrency = "BGN",
            dateTime = timeNowLocal(),
            description = null,
            category = null,
            account = Account(name = "phyre", Orange.toArgb()),
            toAccount = null,
            amount = 0.0,
            dueDate = null,
            transactionType = TransactionType.INCOME,
            customExchangeRateState = CustomExchangeRateState(),

            categories = persistentListOf(),
            accounts = persistentListOf(),

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
            onSetDate = {},
            onSetTime = {},
            onSetTransactionType = {})
    }
}