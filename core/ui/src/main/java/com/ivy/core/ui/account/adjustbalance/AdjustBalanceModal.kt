package com.ivy.core.ui.account.adjustbalance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.domain.pure.dummy.dummyValue
import com.ivy.core.ui.account.adjustbalance.data.AdjustType
import com.ivy.core.ui.amount.AmountModal
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.data.Value
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.Caption
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l2_components.modal.scope.ModalScope
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.design.util.thenWhen

@Composable
fun BoxScope.AdjustBalanceModal(
    modal: IvyModal,
    level: Int = 1,
    balance: Value,
    accountId: String,
) {
    val viewModel: AdjustBalanceViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel, preview = ::previewState)

    LaunchedEffect(accountId) {
        viewModel?.onEvent(
            AdjustBalanceEvent.Initial(
                accountId = accountId,
            )
        )
    }

    val calculatorVisible = remember { mutableStateOf(false) }

    AmountModal(
        modal = modal,
        level = level,
        calculatorVisible = calculatorVisible,
        contentAbove = {
            Header(
                type = state.adjustType,
                onAdjustTypeChange = {
                    viewModel?.onEvent(AdjustBalanceEvent.AdjustTypeChange(it))
                }
            )
        },
        initialAmount = balance,
        onAmountEnter = {
            viewModel?.onEvent(
                AdjustBalanceEvent.AdjustBalance(
                    balance = it,
                )
            )
        }
    )
}

@Composable
private fun ModalScope.Header(
    type: AdjustType,
    onAdjustTypeChange: (AdjustType) -> Unit,
) {
    Title(text = "Adjust balance")
    SpacerVer(height = 8.dp)
    AdjustType(
        type = type,
        onAdjustTypeChange = onAdjustTypeChange,
    )
    SpacerVer(height = 12.dp)
}

@Composable
private fun AdjustType(
    type: AdjustType,
    onAdjustTypeChange: (AdjustType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AdjustTypeButton(
            modifier = Modifier.weight(1f),
            title = "Transaction",
            desc = "Adjust transaction will be created.",
            selected = type == AdjustType.WithTransaction
        ) {
            onAdjustTypeChange(AdjustType.WithTransaction)
        }
        SpacerHor(width = 8.dp)
        AdjustTypeButton(
            modifier = Modifier.weight(1f),
            title = "Artificially",
            desc = "No transaction will be created.",
            selected = type == AdjustType.NoTransaction
        ) {
            onAdjustTypeChange(AdjustType.NoTransaction)
        }
    }
}

@Composable
private fun AdjustTypeButton(
    title: String,
    desc: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(UI.shapes.squared)
            .thenWhen {
                when (selected) {
                    true -> background(UI.colors.primary, UI.shapes.squared)
                    false -> border(1.dp, UI.colors.primary, UI.shapes.squared)
                }
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        val textColor = if (selected)
            rememberContrast(UI.colors.primary) else UI.colorsInverted.pure
        B2(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            color = textColor,
            maxLines = 1,
        )
        SpacerVer(height = 4.dp)
        Caption(
            modifier = Modifier.fillMaxWidth(),
            text = desc,
            color = if (selected) textColor else UI.colors.neutral,
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        AdjustBalanceModal(
            modal = modal,
            balance = dummyValue(0.0),
            accountId = "",
        )
    }
}

private fun previewState() = AdjustBalanceState(
    adjustType = AdjustType.WithTransaction,
)
// endregion