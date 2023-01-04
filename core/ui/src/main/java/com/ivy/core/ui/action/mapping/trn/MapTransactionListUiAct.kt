package com.ivy.core.ui.action.mapping.trn

import android.content.Context
import com.ivy.common.time.format
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.ui.R
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.action.mapping.MapUiAction
import com.ivy.core.ui.action.mapping.account.MapAccountUiAct
import com.ivy.core.ui.data.transaction.*
import com.ivy.data.Value
import com.ivy.data.transaction.DueSection
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionsList
import com.ivy.data.transaction.TrnListItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MapTransactionListUiAct @Inject constructor(
    private val mapAccountUiAct: MapAccountUiAct,
    private val mapCategoryUiAct: MapCategoryUiAct,
    private val mapTrnTimeUiAct: MapTrnTimeUiAct,
    @ApplicationContext
    private val appContext: Context,
    private val timeProvider: TimeProvider
) : MapUiAction<TransactionsList, TransactionsListUi>() {
    override suspend fun transform(domain: TransactionsList): TransactionsListUi =
        TransactionsListUi(
            upcoming = mapDueSection(
                dueType = DueSectionUiType.Upcoming,
                domain = domain.upcoming
            ),
            overdue = mapDueSection(
                dueType = DueSectionUiType.Overdue,
                domain = domain.overdue
            ),
            history = domain.history.map { mapTrnListItem(it) }
        )


    private suspend fun mapDueSection(
        dueType: DueSectionUiType,
        domain: DueSection?,
    ): DueSectionUi? {
        fun formatNonZero(value: Value): ValueUi? =
            if (value.amount > 0.0) format(value, shortenFiat = false) else null

        return domain?.let {
            DueSectionUi(
                dueType = dueType,
                income = formatNonZero(value = domain.income),
                expense = formatNonZero(value = domain.expense),
                trns = domain.trns.map { mapTrnListItem(it) }
            )
        }
    }

    private suspend fun mapTrnListItem(domain: TrnListItem): TrnListItemUi = when (domain) {
        is TrnListItem.DateDivider -> mapDateDivider(domain)
        is TrnListItem.Transfer -> TrnListItemUi.Transfer(
            batchId = domain.batchId,
            time = mapTrnTimeUiAct(domain.time),
            from = mapTransaction(domain.from),
            to = mapTransaction(domain.to),
            fee = domain.fee?.let { mapTransaction(it) }
        )
        is TrnListItem.Trn -> TrnListItemUi.Trn(mapTransaction(domain.trn))
    }

    private fun mapDateDivider(
        domain: TrnListItem.DateDivider
    ): TrnListItemUi.DateDivider {
        val today = timeProvider.dateNow()

        return TrnListItemUi.DateDivider(
            id = domain.id,
            date = domain.date.format(
                if (today.year == domain.date.year) "MMMM dd." else "MMM dd. yyyy"
            ),
            day = when (domain.date) {
                today -> appContext.getString(R.string.today)
                today.minusDays(1) -> appContext.getString(R.string.yesterday)
                today.plusDays(1) -> appContext.getString(R.string.tomorrow)
                else -> null
            } ?: today.format("EEEE"),
            cashflow = format(value = domain.cashflow, shortenFiat = true),
            positiveCashflow = domain.cashflow.amount > 0,
            collapsed = domain.collapsed,
        )
    }

    private suspend fun mapTransaction(domain: Transaction): TransactionUi = TransactionUi(
        id = domain.id.toString(),
        type = domain.type,
        value = format(value = domain.value, shortenFiat = false),
        account = mapAccountUiAct(domain.account),
        category = domain.category?.let { mapCategoryUiAct(it) },
        time = mapTrnTimeUiAct(domain.time),
        title = domain.title,
        description = domain.description,
    )
}