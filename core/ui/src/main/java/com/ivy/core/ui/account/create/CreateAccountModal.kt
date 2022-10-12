package com.ivy.core.ui.account.create

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.account.create.components.AccountCurrency
import com.ivy.core.ui.account.create.components.ExcludeAccount
import com.ivy.core.ui.account.create.components.ExcludedAccInfoModal
import com.ivy.core.ui.color.ColorButton
import com.ivy.core.ui.color.picker.ColorPickerModal
import com.ivy.core.ui.components.ItemIconNameRow
import com.ivy.core.ui.currency.CurrencyPickerModal
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.core.ui.icon.picker.IconPickerModal
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.DividerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.ButtonVisibility
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe

@Composable
fun BoxScope.CreateAccountModal(
    modal: IvyModal,
    level: Int = 1
) {
    val viewModel: CreateAccountViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    val iconPickerModal = rememberIvyModal()
    val colorPickerModal = rememberIvyModal()
    val currencyPickerModal = rememberIvyModal()
    val excludedAccInfoModal = rememberIvyModal()


    val primary = UI.colors.primary
    var color by remember(primary) { mutableStateOf(primary) }
    var excluded by remember { mutableStateOf(false) }

    Modal(
        modal = modal,
        level = level,
        actions = {
            Positive(text = stringResource(R.string.add_account)) {
                viewModel?.onEvent(
                    CreateAccountModalEvent.CreateAccount(
                        color = color,
                        excluded = excluded,
                    )
                )
                modal.hide()
            }
        }
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Title(text = stringResource(R.string.new_account))
                SpacerVer(height = 24.dp)
            }
            item {
                ItemIconNameRow(
                    icon = state.icon,
                    color = color,
                    initialName = "",
                    nameInputHint = stringResource(R.string.new_account),
                    onPickIcon = { iconPickerModal.show() },
                    onNameChange = { viewModel?.onEvent(CreateAccountModalEvent.NameChange(it)) }
                )
                SpacerVer(height = 16.dp)
            }
            item {
                ColorButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = color
                ) {
                    colorPickerModal.show()
                }
                SpacerVer(height = 16.dp)
            }
            item {
                AccountCurrency(
                    currency = state.currency,
                    onPickCurrency = { currencyPickerModal.show() }
                )
            }
            item {
                SpacerVer(height = 24.dp)
                DividerHor()
                SpacerVer(height = 12.dp)
            }
            item {
                ExcludeAccount(
                    excluded = excluded,
                    onMoreInfo = { excludedAccInfoModal.show() },
                    onExcludedChange = { excluded = it }
                )
            }
            item {
                SpacerVer(height = 48.dp) // last spacer
            }
        }
    }

    IconPickerModal(
        modal = iconPickerModal,
        initialIcon = state.icon,
        color = color,
        onIconPick = { viewModel?.onEvent(CreateAccountModalEvent.IconPick(it)) }
    )

    ColorPickerModal(
        modal = colorPickerModal,
        initialColor = color,
        onColorPicked = { color = it }
    )

    CurrencyPickerModal(
        modal = currencyPickerModal,
        initialCurrency = state.currency,
        onCurrencyPick = {
            viewModel?.onEvent(CreateAccountModalEvent.CurrencyChange(it))
        }
    )

    ExcludedAccInfoModal(modal = excludedAccInfoModal)
}

@Composable
private fun AdjustBalance(
    onClick: () -> Unit
) {
    IvyButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        size = ButtonSize.Big,
        visibility = ButtonVisibility.Low,
        feeling = ButtonFeeling.Positive,
        text = stringResource(R.string.adjust_balance),
        icon = null,
        onClick = onClick
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        CreateAccountModal(modal = modal)
    }
}

private fun previewState() = CreateAccountModalState(
    currency = "USD",
    icon = dummyIconSized(R.drawable.ic_custom_account_m)
)
// endregion
