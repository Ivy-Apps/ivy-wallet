package com.ivy.wallet.ui.theme.modal.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.AccountOld
import com.ivy.data.IvyCurrency
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.domain.deprecated.logic.model.CreateAccountData
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.components.IvyCheckboxWithText
import com.ivy.wallet.ui.theme.components.IvyColorPicker
import com.ivy.wallet.ui.theme.modal.*
import com.ivy.wallet.utils.isNotNullOrBlank
import com.ivy.wallet.utils.selectEndTextFieldValue
import com.ivy.wallet.utils.toLowerCaseLocal
import com.ivy.wallet.utils.toUpperCaseLocal
import java.util.*

data class AccountModalData(
    val account: AccountOld?,
    val baseCurrency: String,
    val balance: Double,
    val adjustBalanceMode: Boolean = false,
    val forceNonZeroBalance: Boolean = false,
    val autoFocusKeyboard: Boolean = true,
    val id: UUID = UUID.randomUUID()
)

@Composable
fun BoxWithConstraintsScope.AccountModal(
    modal: AccountModalData?,
    onCreateAccount: (CreateAccountData) -> Unit,
    onEditAccount: (AccountOld, balance: Double) -> Unit,
    dismiss: () -> Unit,
) {
    val account = modal?.account
    var nameTextFieldValue by remember(modal) {
        mutableStateOf(selectEndTextFieldValue(account?.name))
    }
    var color by remember(modal) {
        mutableStateOf(account?.color?.let { Color(it) } ?: Ivy)
    }
    var amount by remember(modal) {
        mutableStateOf(modal?.balance ?: 0.0)
    }
    var currencyCode by remember(modal) {
        mutableStateOf(account?.currency ?: modal?.baseCurrency ?: "")
    }
    var icon by remember(modal) {
        mutableStateOf(account?.icon)
    }
    var includeInBalance by remember(modal) {
        mutableStateOf(account?.includeInBalance ?: true)
    }


    var amountModalVisible by remember { mutableStateOf(false) }
    var currencyModalVisible by remember { mutableStateOf(false) }
    var chooseIconModalVisible by remember(modal) {
        mutableStateOf(false)
    }

    val forceNonZeroBalance = modal?.forceNonZeroBalance ?: false

    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        shiftIfKeyboardShown = false,
        PrimaryAction = {
            ModalAddSave(
                item = modal?.account,
                enabled = nameTextFieldValue.text.isNotNullOrBlank() && (!forceNonZeroBalance || amount > 0)
            ) {
                save(
                    account = account,
                    nameTextFieldValue = nameTextFieldValue,
                    currency = currencyCode,
                    color = color,
                    icon = icon,
                    amount = amount,
                    includeInBalance = includeInBalance,

                    onCreateAccount = onCreateAccount,
                    onEditAccount = onEditAccount,
                    dismiss = dismiss
                )
            }
        }
    ) {
        onScreenStart {
            if (modal?.adjustBalanceMode == true) {
                amountModalVisible = true
            }
        }

        Spacer(Modifier.height(32.dp))

        ModalTitle(
            text = if (modal?.account != null) stringResource(R.string.edit_account) else stringResource(
                R.string.new_account
            ),
        )

        Spacer(Modifier.height(24.dp))

        IconNameRow(
            hint = stringResource(R.string.account_name),
            defaultIcon = R.drawable.ic_custom_account_m,
            color = color,
            icon = icon,

            autoFocusKeyboard = modal?.autoFocusKeyboard ?: true,

            nameTextFieldValue = nameTextFieldValue,
            setNameTextFieldValue = { nameTextFieldValue = it },
            showChooseIconModal = {
                chooseIconModalVisible = true
            }
        )

        Spacer(Modifier.height(24.dp))

        IvyColorPicker(
            selectedColor = color,
            onColorSelected = { color = it }
        )

        Spacer(modifier = Modifier.height(40.dp))

        ModalAmountSection(
            Header = {
                Spacer(Modifier.height(16.dp))

                AccountCurrency(
                    currencyCode = currencyCode
                ) {
                    currencyModalVisible = true
                }

                Spacer(modifier = Modifier.height(16.dp))

                IvyCheckboxWithText(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.Start),
                    text = stringResource(R.string.include_account),
                    checked = includeInBalance
                ) {
                    includeInBalance = it
                }
            },
            label = stringResource(R.string.enter_account_balance).uppercase(),
            currency = currencyCode,
            amount = amount,
            amountPaddingTop = 40.dp,
            amountPaddingBottom = 40.dp,
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
        currency = currencyCode,
        initialAmount = amount,
        dismiss = { amountModalVisible = false }
    ) { newAmount ->
        amount = newAmount

        if (modal?.adjustBalanceMode == true) {
            save(
                account = account,
                nameTextFieldValue = nameTextFieldValue,
                currency = currencyCode,
                color = color,
                icon = icon,
                amount = newAmount,
                includeInBalance = includeInBalance,

                onCreateAccount = onCreateAccount,
                onEditAccount = onEditAccount,
                dismiss = dismiss
            )
        }
    }

    val context = LocalContext.current
    CurrencyModal(
        title = stringResource(R.string.choose_currency),
        initialCurrency = IvyCurrency.fromCode(currencyCode),
        visible = currencyModalVisible,
        dismiss = { currencyModalVisible = false }
    ) {
        currencyCode = it

//        if (IvyCurrency.fromCode(it)?.isCrypto == true) {
//            if (getCustomIconId(context = context, iconName = it, size = "m") != null) {
//                icon = it
//            }
//        }
    }

    ChooseIconModal(
        visible = chooseIconModalVisible,
        initialIcon = icon ?: "account",
        color = color,
        dismiss = { chooseIconModalVisible = false }
    ) {
        icon = it
    }
}

private fun save(
    account: AccountOld?,
    nameTextFieldValue: TextFieldValue,
    currency: String,
    color: Color,
    icon: String?,
    amount: Double,
    includeInBalance: Boolean,

    onCreateAccount: (CreateAccountData) -> Unit,
    onEditAccount: (AccountOld, balance: Double) -> Unit,
    dismiss: () -> Unit
) {
    if (account != null) {
        onEditAccount(
            account.copy(
                name = nameTextFieldValue.text.trim(),
                currency = currency,
                includeInBalance = includeInBalance,
                icon = icon,
                color = color.toArgb()
            ),
            amount
        )
    } else {
        onCreateAccount(
            CreateAccountData(
                name = nameTextFieldValue.text.trim(),
                currency = currency,
                color = color,
                icon = icon,
                balance = amount,
                includeBalance = includeInBalance
            )
        )
    }

    dismiss()
}

@Composable
private fun AccountCurrency(
    currencyCode: String,

    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .background(UI.colors.medium, UI.shapes.r4)
            .clip(UI.shapes.r4)
            .clickable {
                onClick()
            }
            .padding(vertical = 24.dp)
            .testTag("account_modal_currency"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(32.dp))

        Text(
            text = currencyCode.toUpperCaseLocal(),
            style = UI.typo.b1.style(
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.weight(1f))

        val currencyName = IvyCurrency.fromCode(currencyCode)?.name ?: ""
        Text(
            text = "-${currencyName}".toLowerCaseLocal(),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.SemiBold,
                color = Gray
            )
        )

        Spacer(Modifier.width(24.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    com.ivy.core.ui.temp.Preview {
        AccountModal(
            modal = AccountModalData(
                account = null,
                baseCurrency = "BGN",
                balance = 0.0
            ),
            onCreateAccount = { },
            onEditAccount = { _, _ -> }) {

        }
    }
}