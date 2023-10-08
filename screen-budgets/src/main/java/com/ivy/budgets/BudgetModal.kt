package com.ivy.budgets

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Budget
import com.ivy.legacy.datamodel.Category
import com.ivy.domain.legacy.ui.theme.components.ListItem
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.legacy.ui.theme.modal.ModalNameInput
import com.ivy.legacy.utils.isNotNullOrBlank
import com.ivy.legacy.utils.selectEndTextFieldValue
import com.ivy.resources.R
import com.ivy.wallet.domain.deprecated.logic.model.CreateBudgetData
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.Purple1Dark
import com.ivy.wallet.ui.theme.Red3Light
import com.ivy.wallet.ui.theme.modal.DeleteModal
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalAddSave
import com.ivy.wallet.ui.theme.modal.ModalAmountSection
import com.ivy.wallet.ui.theme.modal.ModalDelete
import com.ivy.wallet.ui.theme.modal.ModalTitle
import com.ivy.wallet.ui.theme.modal.edit.AmountModal
import com.ivy.wallet.ui.theme.toComposeColor
import java.util.UUID

@Deprecated("Old design system. Use `:ivy-design` and Material3")
data class BudgetModalData(
    val budget: Budget?,

    val baseCurrency: String,
    val categories: List<Category>,
    val accounts: List<Account>,

    val id: UUID = UUID.randomUUID(),
    val autoFocusKeyboard: Boolean = true,
)

@Deprecated("Old design system. Use `:ivy-design` and Material3")
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
        mutableDoubleStateOf(initialBudget?.amount ?: 0.0)
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
                            categoryIdsSerialized = Budget.serialize(categoryIds),
                            accountIdsSerialized = Budget.serialize(accountIds)
                        )
                    )
                } else {
                    onCreate(
                        CreateBudgetData(
                            name = nameTextFieldValue.text.trim(),
                            amount = amount,
                            categoryIdsSerialized = Budget.serialize(categoryIds),
                            accountIdsSerialized = Budget.serialize(accountIds)
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
                text = if (modal?.budget != null) stringResource(
                    R.string.edit_budget
                ) else stringResource(R.string.create_budget)
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
private fun CategoriesRow(
    categories: List<Category>,
    budgetCategoryIds: List<UUID>,

    onSetBudgetCategoryIds: (List<UUID>) -> Unit,
) {
    Text(
        modifier = Modifier.padding(start = 32.dp),
        text = determineBudgetType(budgetCategoryIds.size),
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
                    // remove category
                    onSetBudgetCategoryIds(budgetCategoryIds.filter { it != category.id })
                } else {
                    // add category
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
