package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.*
import com.ivy.base.R
import com.ivy.data.Account
import com.ivy.data.Budget
import com.ivy.data.Category
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.old.ListItem
import com.ivy.wallet.domain.deprecated.logic.model.CreateBudgetData
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.Purple1Dark
import com.ivy.wallet.ui.theme.Red3Light
import com.ivy.wallet.ui.theme.components.IvyNameTextField
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.ui.theme.toComposeColor
import com.ivy.wallet.utils.hideKeyboard
import com.ivy.wallet.utils.isNotNullOrBlank
import com.ivy.wallet.utils.selectEndTextFieldValue
import java.util.*


data class BudgetModalData(
    val budget: Budget?,

    val baseCurrency: String,
    val categories: List<Category>,
    val accounts: List<Account>,

    val id: UUID = UUID.randomUUID(),
    val autoFocusKeyboard: Boolean = true,
)

@Composable
fun BoxWithConstraintsScope.BudgetModal(
    modal: BudgetModalData?,

    onCreate: (CreateBudgetData) -> Unit,
    onEdit: (Budget) -> Unit,
    onDelete: (Budget) -> Unit,
    dismiss: () -> Unit
) {
    val initialBudget = modal?.budget
    var nameTextFieldValue by remember(modal) {
        mutableStateOf(selectEndTextFieldValue(initialBudget?.name))
    }
    var amount by remember(modal) {
        mutableStateOf(initialBudget?.amount ?: 0.0)
    }
    var categoryIds by remember(modal) {
        mutableStateOf(modal?.budget?.parseCategoryIds() ?: emptyList())
    }
    var accountIds by remember(modal) {
        mutableStateOf(modal?.budget?.parseAccountIds() ?: emptyList())
    }


    var amountModalVisible by remember(modal) { mutableStateOf(false) }
    var deleteModalVisible by remember(modal) { mutableStateOf(false) }


    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        PrimaryAction = {
            ModalAddSave(
                item = modal?.budget,
                enabled = nameTextFieldValue.text.isNotNullOrBlank() && amount > 0.0
            ) {
                if (initialBudget != null) {
                    onEdit(
                        initialBudget.copy(
                            name = nameTextFieldValue.text.trim(),
                            amount = amount,
                            categoryIdsSerialized = BudgetExt.serialize(categoryIds),
                            accountIdsSerialized = BudgetExt.serialize(accountIds)
                        )
                    )
                } else {
                    onCreate(
                        CreateBudgetData(
                            name = nameTextFieldValue.text.trim(),
                            amount = amount,
                            categoryIdsSerialized = BudgetExt.serialize(categoryIds),
                            accountIdsSerialized = BudgetExt.serialize(accountIds)
                        )
                    )
                }

                dismiss()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModalTitle(
                text = if (modal?.budget != null) stringResource(R.string.edit_budget) else stringResource(
                    R.string.create_budget
                )
            )

            if (initialBudget != null) {
                Spacer(Modifier.weight(1f))

                ModalDelete {
                    deleteModalVisible = true
                }

                Spacer(Modifier.width(24.dp))
            }
        }


        Spacer(Modifier.height(24.dp))

        ModalNameInput(
            hint = stringResource(R.string.budget_name),
            autoFocusKeyboard = modal?.autoFocusKeyboard ?: true,
            textFieldValue = nameTextFieldValue,
            setTextFieldValue = {
                nameTextFieldValue = it
            }
        )

        Spacer(Modifier.height(24.dp))

        CategoriesRow(
            categories = modal?.categories ?: emptyList(),
            budgetCategoryIds = categoryIds,
            onSetBudgetCategoryIds = {
                categoryIds = it
            }
        )

        Spacer(Modifier.height(24.dp))

        ModalAmountSection(
            label = stringResource(R.string.budget_amount_uppercase),
            currency = modal?.baseCurrency ?: "",
            amount = amount,
            amountPaddingTop = 24.dp,
            amountPaddingBottom = 0.dp
        ) {
            amountModalVisible = true
        }
    }

    val amountModalId = remember(modal, amount) {
        UUID.randomUUID()
    }
    AmountModal(
        id = amountModalId,
        visible = amountModalVisible,
        currency = modal?.baseCurrency ?: "",
        initialAmount = amount,
        dismiss = { amountModalVisible = false }
    ) {
        amount = it
    }

    DeleteModal(
        visible = deleteModalVisible,
        title = stringResource(R.string.confirm_deletion),
        description = stringResource(
            R.string.confirm_budget_deletion_warning,
            nameTextFieldValue.text
        ),
        dismiss = { deleteModalVisible = false }
    ) {
        if (initialBudget != null) {
            onDelete(initialBudget)
        }
        deleteModalVisible = false
        dismiss()
    }
}

@Composable
fun ModalNameInput(
    hint: String,
    autoFocusKeyboard: Boolean,

    textFieldValue: TextFieldValue,
    setTextFieldValue: (TextFieldValue) -> Unit,
) {
    val nameFocus = FocusRequester()

    onScreenStart {
        if (autoFocusKeyboard) {
            nameFocus.requestFocus()
        }
    }

    val view = LocalView.current
    IvyNameTextField(
        modifier = Modifier
            .padding(start = 32.dp, end = 36.dp)
            .focusRequester(nameFocus),
        underlineModifier = Modifier.padding(start = 32.dp, end = 32.dp),
        value = textFieldValue,
        hint = hint,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text,
            autoCorrect = true
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                hideKeyboard(view)
            }
        ),
    ) { newValue ->
        setTextFieldValue(newValue)
    }
}

@Composable
private fun CategoriesRow(
    categories: List<Category>,
    budgetCategoryIds: List<UUID>,

    onSetBudgetCategoryIds: (List<UUID>) -> Unit,
) {
    Text(
        modifier = Modifier.padding(start = 32.dp),
        text = BudgetExt.type(budgetCategoryIds.size),
        style = UI.typo.b1.style(
            fontWeight = FontWeight.Medium,
            color = UI.colors.pureInverse
        )
    )

    Spacer(Modifier.height(16.dp))

    LazyRow(
        modifier = Modifier.testTag("budget_categories_row")
    ) {
        item {
            Spacer(Modifier.width(24.dp))
        }

        items(items = categories) { category ->
            ListItem(
                icon = category.icon,
                defaultIcon = R.drawable.ic_custom_category_s,
                text = category.name,
                selectedColor = category.color.toComposeColor().takeIf {
                    budgetCategoryIds.contains(category.id)
                }
            ) { selected ->
                if (selected) {
                    //remove category
                    onSetBudgetCategoryIds(budgetCategoryIds.filter { it != category.id })
                } else {
                    //add category
                    onSetBudgetCategoryIds(budgetCategoryIds.plus(category.id))
                }
            }
        }

        item {
            Spacer(Modifier.width(24.dp))
        }
    }
}

@Preview
@Composable
private fun Preview_create() {
    IvyWalletPreview {
        val cat1 = Category("Science", color = Purple1Dark.toArgb(), icon = "atom")

        BudgetModal(
            modal = BudgetModalData(
                budget = null,
                baseCurrency = "BGN",
                categories = listOf(
                    cat1,
                    Category("Pet", color = Red3Light.toArgb(), icon = "pet"),
                    Category("Home", color = Green.toArgb(), icon = null),
                ),
                accounts = emptyList()
            ),
            onCreate = {},
            onEdit = {},
            onDelete = {}
        ) {

        }
    }
}

@Preview
@Composable
private fun Preview_edit() {
    IvyWalletPreview {
        val cat1 = Category("Science", color = Purple1Dark.toArgb(), icon = "atom")

        BudgetModal(
            modal = BudgetModalData(
                budget = Budget(
                    name = "Shopping",
                    amount = 1250.0,
                    accountIdsSerialized = null,
                    categoryIdsSerialized = null,
                    orderId = 0.0
                ),
                baseCurrency = "BGN",
                categories = listOf(
                    cat1,
                    Category("Pet", color = Red3Light.toArgb(), icon = "pet"),
                    Category("Home", color = Green.toArgb(), icon = null),
                ),
                accounts = emptyList()
            ),
            onCreate = {},
            onEdit = {},
            onDelete = {}
        ) {

        }
    }
}