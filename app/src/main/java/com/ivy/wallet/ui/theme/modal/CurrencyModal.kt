package com.ivy.wallet.ui.theme.modal

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.model.IvyCurrency
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.components.CurrencyPicker
import com.ivy.wallet.ui.theme.style
import java.util.*

@Composable
fun BoxWithConstraintsScope.CurrencyModal(
    title: String,
    initialCurrency: IvyCurrency?,
    visible: Boolean,
    dismiss: () -> Unit,
    id: UUID = UUID.randomUUID(),

    onSetCurrency: (String) -> Unit
) {
    var currency by remember(id) {
        mutableStateOf(initialCurrency ?: IvyCurrency.getDefault())
    }

    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSave(
                modifier = Modifier.testTag("set_currency_save")
            ) {
                onSetCurrency(currency.code)
                dismiss()
            }
        },
        includeActionsRowPadding = false,
        scrollState = null
    ) {
        var keyboardVisible by remember {
            mutableStateOf(false)
        }

        if (!keyboardVisible) {
            Spacer(Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModalTitle(text = title)

                Spacer(Modifier.weight(1f))

                Text(
                    text = "supports crypto",
                    style = Typo.caption.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = Gray
                    )
                )

                Spacer(Modifier.width(32.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        CurrencyPicker(
            modifier = Modifier
                .weight(1f),
            initialSelectedCurrency = currency,

            includeKeyboardShownInsetSpacer = false,
            lastItemSpacer = 120.dp,
            onKeyboardShown = { visible ->
                keyboardVisible = visible
            }
        ) {
            currency = it
        }
    }
}

@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        CurrencyModal(
            title = "Set currency",
            initialCurrency = null,
            visible = true,
            dismiss = {}
        ) {

        }
    }
}