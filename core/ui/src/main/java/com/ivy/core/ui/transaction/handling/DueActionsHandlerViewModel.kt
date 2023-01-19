package com.ivy.core.ui.transaction.handling

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.toUUID
import com.ivy.core.domain.HandlerViewModel
import com.ivy.core.domain.action.transaction.TrnByIdAct
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.action.transaction.transfer.ModifyTransfer
import com.ivy.core.domain.action.transaction.transfer.TransferByBatchIdAct
import com.ivy.core.domain.action.transaction.transfer.WriteTransferAct
import com.ivy.core.ui.algorithm.trnhistory.data.TransactionUi
import com.ivy.core.ui.algorithm.trnhistory.data.TransferUi
import com.ivy.data.transaction.TrnTime
import com.ivy.design.util.hiltViewModelPreviewSafe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Immutable
data class DueActionsHandler(
    val onExecuteTrn: (TransactionUi) -> Unit,
    val onSkipTrn: (TransactionUi) -> Unit,
    val onExecuteTransfer: (TransferUi) -> Unit,
    val onSkipTransfer: (TransferUi) -> Unit,
)

sealed interface DueActionEvent {
    data class SkipTrn(val trn: TransactionUi) : DueActionEvent
    data class ExecuteTrn(val trn: TransactionUi) : DueActionEvent
    data class SkipTransfer(val trn: TransferUi) : DueActionEvent
    data class ExecuteTransfer(val transfer: TransferUi) : DueActionEvent
}

@HiltViewModel
class DueActionsHandlerViewModel @Inject constructor(
    private val writeTransferAct: WriteTransferAct,
    private val writeTrnsAct: WriteTrnsAct,
    private val transferByBatchIdAct: TransferByBatchIdAct,
    private val trnByIdAct: TrnByIdAct,
    private val timeProvider: TimeProvider,
) : HandlerViewModel<DueActionEvent>() {
    override suspend fun handleEvent(event: DueActionEvent) = when (event) {
        is DueActionEvent.ExecuteTransfer -> handleExecuteTransfer(event)
        is DueActionEvent.SkipTransfer -> handleSkipTransfer(event)
        is DueActionEvent.ExecuteTrn -> handleExecuteTrn(event)
        is DueActionEvent.SkipTrn -> handleSkipTrn(event)
    }

    private suspend fun handleExecuteTransfer(event: DueActionEvent.ExecuteTransfer) {
        writeTransferAct(
            ModifyTransfer.updateTrnTime(
                batchId = event.transfer.batchId,
                newTrnTime = TrnTime.Actual(timeProvider.timeNow()),
            )
        )
    }

    private suspend fun handleSkipTransfer(event: DueActionEvent.SkipTransfer) {
        val transfer = transferByBatchIdAct(event.trn.batchId) ?: return
        writeTransferAct(ModifyTransfer.delete(transfer))
    }

    private suspend fun handleExecuteTrn(event: DueActionEvent.ExecuteTrn) {
        val old = trnByIdAct(event.trn.id.toUUID()) ?: return
        writeTrnsAct(
            WriteTrnsAct.Input.Update(
                old = old,
                new = old.copy(time = TrnTime.Actual(timeProvider.timeNow()))
            )
        )
    }

    private suspend fun handleSkipTrn(event: DueActionEvent.SkipTrn) {
        writeTrnsAct(WriteTrnsAct.Input.DeleteInefficient(trnId = event.trn.id))
    }
}

@Composable
fun defaultDueActionsHandler(): DueActionsHandler {
    val viewModel: DueActionsHandlerViewModel? = hiltViewModelPreviewSafe()

    return DueActionsHandler(
        onExecuteTrn = {
            viewModel?.onEvent(DueActionEvent.ExecuteTrn(it))
        },
        onSkipTrn = {
            viewModel?.onEvent(DueActionEvent.SkipTrn(it))
        },
        onExecuteTransfer = {
            viewModel?.onEvent(DueActionEvent.ExecuteTransfer(it))
        },

        onSkipTransfer = {
            viewModel?.onEvent(DueActionEvent.SkipTransfer(it))
        },
    )
}