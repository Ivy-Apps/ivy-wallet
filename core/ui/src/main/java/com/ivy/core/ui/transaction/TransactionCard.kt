package com.ivy.core.ui.transaction

import com.ivy.data.transaction.Transaction
import javax.annotation.concurrent.Immutable

//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.Stable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.toArgb
//import androidx.compose.ui.platform.testTag
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.ivy.base.R
//import com.ivy.common.mapToTrnType
//import com.ivy.common.timeNowUTC
//import com.ivy.core.domain.functions.dummy.*
//import com.ivy.core.ui.account.Badge
//import com.ivy.core.ui.category.Badge
//import com.ivy.core.ui.icon.ItemIcon
//import ComponentPreview
//import com.ivy.core.ui.time.formatNicely
//import com.ivy.core.ui.transaction.util.TrnDetailedType
//import com.ivy.core.ui.transaction.util.TrnDetailedType.*
//import com.ivy.core.ui.transaction.util.detailedType
//import com.ivy.core.ui.value.AmountCurrency
//import com.ivy.core.ui.value.formatAmount
//import com.ivy.data.account.Account
//import com.ivy.data.category.Category
//import com.ivy.data.icon.IconSize
//import com.ivy.data.transaction.Transaction
//import com.ivy.data.transaction.TrnTime
//import com.ivy.data.Value
//import com.ivy.design.l0_system.*
//import com.ivy.design.l1_buildingBlocks.IvyIcon
//import com.ivy.design.l1_buildingBlocks.IvyText
//import com.ivy.design.l1_buildingBlocks.SpacerHor
//import com.ivy.design.l1_buildingBlocks.SpacerVer
//import com.ivy.frp.view.navigation.navigation
//import com.ivy.screens.EditTransaction
//import com.ivy.screens.ItemStatistic
//
////region Default actions
//@Composable
//fun defaultOnTrnClick(): (Transaction) -> Unit {
//    val nav = navigation()
//    return { trn ->
//        nav.navigateTo(
//            EditTransaction(
//                initialTransactionId = trn.id,
//                type = mapToTrnType(trn.type),
//            )
//        )
//    }
//}
//
//@Composable
//fun defaultOnAccountClick(): (Account) -> Unit {
//    val nav = navigation()
//    return { acc ->
//        nav.navigateTo(
//            ItemStatistic(
//                accountId = acc.id
//            )
//        )
//    }
//}
//
//@Composable
//fun defaultOnCategoryClick(): (Category) -> Unit {
//    val nav = navigation()
//    return { category ->
//        nav.navigateTo(
//            ItemStatistic(
//                categoryId = category.id
//            )
//        )
//    }
//}
//
//fun dummyDueActions() = DueActions({}, {})
//// endregion
//
@Immutable
data class DueActions(
    val onSkip: (Transaction) -> Unit,
    val onPayGet: (Transaction) -> Unit,
)
//
//@Composable
//fun Transaction.Card(
//    modifier: Modifier = Modifier,
//
//    onClick: (Transaction) -> Unit = defaultOnTrnClick(),
//    onAccountClick: (Account) -> Unit = defaultOnAccountClick(),
//    onCategoryClick: (Category) -> Unit = defaultOnCategoryClick(),
//    dueActions: DueActions? = null,
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .clip(UI.shapes.r4)
//            .background(UI.colors.medium, UI.shapes.r4)
//            .clickable(onClick = {
//                onClick(this@Card)
//            })
//            .padding(all = 20.dp)
//            .testTag("transaction_card")
//    ) {
//        TransactionHeader(
//            type = type,
//            account = account,
//            category = category,
//            onAccountClick = onAccountClick,
//            onCategoryClick = onCategoryClick
//        )
//        val detailedType = detailedType(type = type, time = time)
//        DueDate(time = time, detailedType = detailedType)
//        Title(title = title, time = time)
//        Description(description = description, title = title)
//        TrnAmount(detailedType = detailedType, value = value, time = time)
//        TransferAmountDifferentCurrency(type = type, value = value)
//
//        if (dueActions != null) {
//            DuePaymentCTAs(
//                time = time,
//                type = type,
//                onSkip = {
//                    dueActions.onSkip(this@Card)
//                },
//                onPayGet = {
//                    dueActions.onPayGet(this@Card)
//                }
//            )
//        }
//    }
//}
//
//// region Transaction Header
//@Composable
//private fun TransactionHeader(
//    type: TransactionType,
//    account: Account,
//    category: Category?,
//
//    onAccountClick: (Account) -> Unit,
//    onCategoryClick: (Category) -> Unit
//) {
//    when (type) {
//        is TransactionType.Transfer -> TransferHeader(
//            account = account,
//            toAccount = type.toAccount
//        )
//        else -> IncomeExpenseHeader(
//            account = account,
//            category = category,
//            onAccountClick = onAccountClick,
//            onCategoryClick = onCategoryClick,
//        )
//    }
//}
//
//@Composable
//private fun IncomeExpenseHeader(
//    account: Account,
//    category: Category?,
//
//    onCategoryClick: (Category) -> Unit,
//    onAccountClick: (Account) -> Unit,
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        category?.let {
//            category.Badge(
//                onClick = { onCategoryClick(category) }
//            )
//            SpacerHor(width = 12.dp)
//        }
//
//        account.Badge(
//            background = UI.colors.pure,
//            onClick = { onAccountClick(account) }
//        )
//    }
//}
//
//@Composable
//private fun TransferHeader(
//    account: Account,
//    toAccount: Account
//) {
//    @Composable
//    fun Account.IconName() {
//        icon.ItemIcon(
//            size = IconSize.S,
//            tint = UI.colors.pureInverse,
//        )
//        SpacerHor(width = 4.dp)
//        IvyText(
//            text = name,
//            typo = UI.typo.c.style(
//                color = UI.colors.pureInverse,
//                fontWeight = FontWeight.ExtraBold
//            )
//        )
//    }
//
//    Row(
//        modifier = Modifier
//            .background(UI.colors.pure, UI.shapes.rFull)
//            .padding(start = 8.dp, end = 20.dp)
//            .padding(vertical = 4.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        account.IconName()
//
//        Spacer(Modifier.width(12.dp))
//        IvyIcon(icon = R.drawable.ic_arrow_right)
//        Spacer(Modifier.width(8.dp))
//
//        toAccount.IconName()
//    }
//}
//// endregion
//
//// region Due Date ("DUE ON ...")
//@Composable
//private fun DueDate(
//    time: TrnTime,
//    detailedType: TrnDetailedType
//) {
//    if (time is TrnTime.Due) {
//        SpacerVer(height = 12.dp)
//        // TODO: OPTIMIZATION, move time formatting in ViewModel
//        val formattedTime = remember(time.due) { time.due.formatNicely() }
//        Text(
//            modifier = Modifier.padding(horizontal = 4.dp),
//            text = stringResource(
//                R.string.due_on,
//                formattedTime,
//            ).uppercase(),
//            style = UI.typo.nC.style(
//                color = when (detailedType) {
//                    UpcomingExpense, UpcomingIncome -> Orange
//                    OverdueExpense, OverdueIncome -> Red
//                    else -> Gray
//                },
//                fontWeight = FontWeight.Bold
//            )
//        )
//    }
//}
////endregion
//
//// region Title & Description
//@Composable
//private fun Title(
//    title: String?,
//    time: TrnTime
//) {
//    if (title != null) {
//        SpacerVer(height = if (time is TrnTime.Due) 8.dp else 8.dp)
//        Text(
//            text = title,
//            modifier = Modifier.padding(horizontal = 4.dp),
//            style = UI.typo.b1.style(
//                fontWeight = FontWeight.ExtraBold,
//                color = UI.colors.pureInverse
//            )
//        )
//    }
//}
//
//@Composable
//private fun Description(
//    description: String?,
//    title: String?
//) {
//    if (description != null) {
//        SpacerVer(height = if (title != null) 4.dp else 8.dp)
//        Text(
//            text = description,
//            modifier = Modifier.padding(horizontal = 4.dp),
//            style = UI.typo.nC.style(
//                color = UI.colors.gray,
//                fontWeight = FontWeight.Bold
//            ),
//            maxLines = 3,
//            overflow = TextOverflow.Ellipsis
//        )
//    }
//}
////endregion
//
//// region Type & Amount (+ Transfer amount in different currency)
//@Composable
//private fun TrnAmount(
//    detailedType: TrnDetailedType,
//    value: Value,
//    time: TrnTime
//) {
//    SpacerVer(height = if (time is TrnTime.Due) 12.dp else 12.dp)
//
//    Row(
//        modifier = Modifier
//            .testTag("type_amount_currency")
//            .padding(horizontal = 4.dp), // additional padding to look better?
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        TrnTypeIcon(trnDetailedType = detailedType)
//        SpacerHor(width = 12.dp)
//        value.AmountCurrency(
//            color = when (detailedType) {
//                ActualIncome, OverdueIncome, UpcomingIncome -> Green
//                Transfer -> Purple
//                ActualExpense -> UI.colors.pureInverse
//                UpcomingExpense -> Orange
//                OverdueExpense -> Red
//            }
//        )
//    }
//}
//
//@Composable
//private fun TransferAmountDifferentCurrency(
//    type: TransactionType,
//    value: Value
//) {
//    if (type is TransactionType.Transfer &&
//        value.currency != type.toValue.currency
//    ) {
//        val toAmountFormatted = type.toValue.formatAmount(shortenBigNumbers = false)
//
//        Text(
//            modifier = Modifier.padding(start = 54.dp),
//            text = "$toAmountFormatted ${type.toValue.currency}",
//            style = UI.typo.nB2.style(
//                color = Gray,
//                fontWeight = FontWeight.Normal
//            )
//        )
//    }
//}
//// endregion
//
//// region Due Payment CTAs
//@Composable
//private fun DuePaymentCTAs(
//    time: TrnTime,
//    type: TransactionType,
//    onSkip: () -> Unit,
//    onPayGet: () -> Unit,
//) {
//    if (time is TrnTime.Due) {
//        SpacerVer(height = 12.dp)
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 4.dp), // additional padding to look better
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            SkipButton(onClick = onSkip)
//            SpacerHor(width = 12.dp)
//            PayGetButton(type = type, onClick = onPayGet)
//        }
//    }
//}
//
//@Composable
//private fun RowScope.SkipButton(
//    onClick: () -> Unit
//) {
//    Text(
//        modifier = Modifier
//            .weight(1f)
//            .clip(UI.shapes.rFull)
//            .background(UI.colors.pure, UI.shapes.rFull)
//            .clickable(onClick = onClick)
//            .padding(vertical = 8.dp),
//        text = stringResource(R.string.skip),
//        style = UI.typo.b2.style(
//            color = UI.colors.pureInverse,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center,
//        )
//    )
//}
//
//@Composable
//private fun RowScope.PayGetButton(
//    type: TransactionType,
//    onClick: () -> Unit
//) {
//    val isIncome = type is TransactionType.Income
//    Text(
//        modifier = Modifier
//            .weight(1f)
//            .clip(UI.shapes.rFull)
//            .background(
//                color = if (isIncome) Green else UI.colors.pureInverse,
//                shape = UI.shapes.rFull
//            )
//            .clickable(onClick = onClick)
//            .padding(vertical = 8.dp),
//        text = stringResource(
//            if (isIncome) R.string.get else R.string.pay
//        ),
//        style = UI.typo.b2.style(
//            color = if (isIncome) UI.colors.pure else White,
//            fontWeight = FontWeight.Bold,
//            TextAlign.Center,
//        )
//    )
//}
//// endregion
//
//
//// region Previews
//@Preview
//@Composable
//private fun Preview_ActualExpense_CategoryTitle() {
//    ComponentPreview {
//        val transaction = dummyTrn(
//            amount = 11.89,
//            type = TransactionType.Expense,
//            account = dummyAcc(
//                name = "Revolut",
//                icon = dummyIconSized(R.drawable.ic_custom_revolut_s)
//            ),
//            title = "Green Book",
//            category = dummyCategory(
//                name = "Order food",
//                color = Red2.toArgb(),
//                icon = dummyIconSized(R.drawable.ic_custom_orderfood2_s)
//            )
//        )
//        transaction.Card(
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_ActualIncome_CategoryTitleDescription() {
//    ComponentPreview {
//        val transaction = dummyTrn(
//            amount = 11.89,
//            type = TransactionType.Income,
//            account = dummyAcc(
//                name = "Account",
//                currency = "BGN",
//                icon = dummyIconSized(R.drawable.ic_custom_cash_s)
//            ),
//            title = "Title",
//            description = "Description",
//            category = dummyCategory(
//                name = "Category",
//                color = IvyDark.toArgb(),
//                icon = dummyIconSized(R.drawable.ic_custom_orderfood2_s)
//            )
//        )
//        transaction.Card(
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_Transfer_SameCurrency() {
//    ComponentPreview {
//        val transaction = dummyTrn(
//            amount = 200.0,
//            account = dummyAcc(
//                name = "Bank",
//                currency = "USD",
//                icon = dummyIconUnknown(R.drawable.ic_vue_money_card)
//            ),
//            type = TransactionType.Transfer(
//                toValue = dummyValue(200.0, "USD"),
//                toAccount = dummyAcc(
//                    name = "Revolut",
//                    currency = "USD",
//                    icon = dummyIconSized(R.drawable.ic_custom_revolut_s)
//                )
//            ),
//            title = "Title",
//            category = null
//        )
//        transaction.Card(
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_Transfer_DifferentCurrency() {
//    ComponentPreview {
//        val transaction = dummyTrn(
//            amount = 100_000.0,
//            account = dummyAcc(
//                name = "Bank",
//                currency = "USD",
//                icon = dummyIconUnknown(R.drawable.ic_vue_money_card)
//            ),
//            type = TransactionType.Transfer(
//                toValue = dummyValue(95_000_000.0, "EUR"),
//                toAccount = dummyAcc(
//                    name = "Cash",
//                    currency = "EUR",
//                    icon = dummyIconSized(R.drawable.ic_vue_money_archive)
//                )
//            ),
//            title = "Withdraw cash",
//            category = null
//        )
//        transaction.Card(
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_UpcomingIncome() {
//    ComponentPreview {
//        val transaction = dummyTrn(
//            amount = 11.89,
//            type = TransactionType.Income,
//            account = dummyAcc(
//                name = "Account",
//                currency = "BGN",
//                icon = dummyIconSized(R.drawable.ic_custom_cash_s)
//            ),
//            title = "Title",
//            description = "Description",
//            category = dummyCategory(
//                name = "Category",
//                color = IvyDark.toArgb(),
//                icon = dummyIconSized(R.drawable.ic_custom_orderfood2_s)
//            ),
//            time = dummyDue(timeNowUTC().plusDays(3))
//        )
//        transaction.Card(
//            modifier = Modifier.padding(horizontal = 16.dp),
//            dueActions = dummyDueActions()
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_OverdueIncome() {
//    ComponentPreview {
//        val transaction = dummyTrn(
//            amount = 11.89,
//            type = TransactionType.Income,
//            account = dummyAcc(
//                name = "Account",
//                currency = "BGN",
//                icon = dummyIconSized(R.drawable.ic_custom_cash_s)
//            ),
//            title = "Title",
//            description = "Description",
//            category = dummyCategory(
//                name = "Category",
//                color = IvyDark.toArgb(),
//                icon = dummyIconSized(R.drawable.ic_custom_orderfood2_s)
//            ),
//            time = dummyDue(timeNowUTC().minusDays(3)),
//        )
//        transaction.Card(
//            modifier = Modifier.padding(horizontal = 16.dp),
//            dueActions = dummyDueActions()
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_UpcomingExpense() {
//    ComponentPreview {
//        val transaction = dummyTrn(
//            amount = 11.89,
//            type = TransactionType.Expense,
//            account = dummyAcc(
//                name = "Account",
//                currency = "BGN",
//                icon = dummyIconSized(R.drawable.ic_custom_cash_s)
//            ),
//            title = "Title",
//            description = "Description",
//            category = dummyCategory(
//                name = "Category",
//                color = IvyDark.toArgb(),
//                icon = dummyIconSized(R.drawable.ic_custom_orderfood2_s)
//            ),
//            time = dummyDue(timeNowUTC().plusDays(3))
//        )
//        transaction.Card(
//            modifier = Modifier.padding(horizontal = 16.dp),
//            dueActions = dummyDueActions()
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun Preview_OverdueExpense() {
//    ComponentPreview {
//        val transaction = dummyTrn(
//            amount = 11.89,
//            type = TransactionType.Expense,
//            account = dummyAcc(
//                name = "Account",
//                currency = "BGN",
//                icon = dummyIconSized(R.drawable.ic_custom_cash_s)
//            ),
//            title = "Title",
//            description = "Description",
//            category = dummyCategory(
//                name = "Category",
//                color = IvyDark.toArgb(),
//                icon = dummyIconSized(R.drawable.ic_custom_orderfood2_s)
//            ),
//            time = dummyDue(timeNowUTC().minusDays(3))
//        )
//        transaction.Card(
//            modifier = Modifier.padding(horizontal = 16.dp),
//        )
//    }
//}
//// endregion