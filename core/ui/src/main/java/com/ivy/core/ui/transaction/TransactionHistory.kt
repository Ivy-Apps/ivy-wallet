package com.ivy.core.ui.transaction

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.temp.Preview
import com.ivy.data.transaction.TrnListItem
import com.ivy.design.l1_buildingBlocks.SpacerVer

fun LazyListScope.transactionsList(
    trns: List<TrnListItem>,
    dueActions: DueActions?
) {
    itemsIndexed(
        items = trns,
        key = { _, item ->
            when (item) {
                is TrnListItem.DateDivider -> item.date.toString()
                is TrnListItem.OverdueSection -> "overdue"
                is TrnListItem.UpcomingSection -> "upcoming"
                is TrnListItem.Trn -> item.trn.id.toString()
            }
        }
    ) { index, item ->
        when (item) {
            is TrnListItem.UpcomingSection -> {
                SpacerVer(height = 24.dp)
                item.SectionDivider()
            }
            is TrnListItem.OverdueSection -> {
                SpacerVer(height = 24.dp)
                item.SectionDivider()
            }
            is TrnListItem.DateDivider -> {
                SpacerVer(
                    // not first date divider
                    height = if (index > 0 && trns[index - 1] is TrnListItem.Trn)
                        32.dp else 24.dp
                )
                item.DateDivider()
            }
            is TrnListItem.Trn -> {
                SpacerVer(height = 12.dp)
                item.trn.Card(
                    dueActions = dueActions
                )
            }
        }
    }
}

// region Previews
@Preview
@Composable
private fun Preview() {
    Preview {

    }
}
// endregion