package com.ivy.wallet.ui.planned.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.design.api.navigation
import com.ivy.design.navigation.Navigation
import com.ivy.wallet.R
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.PlannedPaymentRule
import com.ivy.wallet.ui.EditPlanned
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.IvyTheme
import com.ivy.wallet.ui.theme.Typo
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.style
import com.ivy.wallet.ui.theme.transaction.SectionDivider
import kotlin.math.absoluteValue

@Composable
fun PlannedPaymentsLazyColumn(
    Header: @Composable () -> Unit,


    currency: String,
    categories: List<Category>,
    accounts: List<Account>,
    listState: LazyListState = rememberLazyListState(),

    oneTime: List<PlannedPaymentRule>,
    oneTimeIncome: Double,
    oneTimeExpenses: Double,


    recurring: List<PlannedPaymentRule>,
    recurringIncome: Double,
    recurringExpenses: Double,
) {
    val nav = navigation()
    var oneTimeExpanded by remember { mutableStateOf(true) }
    var recurringExpanded by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        item {
            Header()
        }

        plannedPaymentItems(
            nav = nav,
            currency = currency,
            categories = categories,
            accounts = accounts,
            listState = listState,

            oneTime = oneTime,
            oneTimeIncome = oneTimeIncome,
            oneTimeExpenses = oneTimeExpenses,
            oneTimeExpanded = oneTimeExpanded,
            setOneTimeExpanded = {
                oneTimeExpanded = it
            },

            recurring = recurring,
            recurringIncome = recurringIncome,
            recurringExpenses = recurringExpenses,
            recurringExpanded = recurringExpanded,
            setRecurringExpanded = {
                recurringExpanded = it
            }
        )
    }
}

private fun LazyListScope.plannedPaymentItems(
    nav: Navigation,
    currency: String,
    categories: List<Category>,
    accounts: List<Account>,
    listState: LazyListState,

    oneTime: List<PlannedPaymentRule>,
    oneTimeIncome: Double,
    oneTimeExpenses: Double,
    oneTimeExpanded: Boolean,
    setOneTimeExpanded: (Boolean) -> Unit,


    recurring: List<PlannedPaymentRule>,
    recurringIncome: Double,
    recurringExpenses: Double,
    recurringExpanded: Boolean,
    setRecurringExpanded: (Boolean) -> Unit
) {
    if (oneTime.isNotEmpty()) {

        item {
            SectionDivider(
                expanded = oneTimeExpanded,
                setExpanded = setOneTimeExpanded,
                title = "One time payments",
                titleColor = IvyTheme.colors.pureInverse,
                baseCurrency = currency,
                income = oneTimeIncome,
                expenses = oneTimeExpenses.absoluteValue
            )
        }

        if (oneTimeExpanded) {
            itemsIndexed(oneTime) { _, item ->
                PlannedPaymentCard(
                    baseCurrency = currency,
                    categories = categories,
                    accounts = accounts,
                    plannedPayment = item,
                ) { plannedPaymentRule ->
                    onPlannedPaymentClick(
                        nav = nav,
                        listState = listState,
                        rule = plannedPaymentRule
                    )
                }
            }
        }
    }

    if (recurring.isNotEmpty()) {

        item {
            SectionDivider(
                expanded = recurringExpanded,
                setExpanded = setRecurringExpanded,
                title = "Recurring payments",
                titleColor = IvyTheme.colors.pureInverse,
                baseCurrency = currency,
                income = recurringIncome,
                expenses = recurringExpenses.absoluteValue
            )
        }

        if (recurringExpanded) {
            itemsIndexed(recurring) { _, item ->
                PlannedPaymentCard(
                    baseCurrency = currency,
                    categories = categories,
                    accounts = accounts,
                    plannedPayment = item,
                ) { plannedPaymentRule ->
                    onPlannedPaymentClick(
                        nav = nav,
                        listState = listState,
                        rule = plannedPaymentRule
                    )
                }
            }
        }
    }

    if (oneTime.isEmpty() && recurring.isEmpty()) {
        item {
            NoPlannedPaymentsEmptyState()
        }
    }

    item {
        //last spacer - scroll hack
        Spacer(Modifier.height(150.dp))
    }
}

private fun onPlannedPaymentClick(
    nav: Navigation,
    listState: LazyListState,
    rule: PlannedPaymentRule
) {
    nav.navigateTo(
        EditPlanned(
            plannedPaymentRuleId = rule.id,
            type = rule.type
        )
    )
}



@Composable
private fun LazyItemScope.NoPlannedPaymentsEmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))

        IvyIcon(
            icon = R.drawable.ic_planned_payments,
            tint = Gray
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "No planned payments",
            style = Typo.body1.style(
                color = Gray,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "You don't have any planed payments.\nPress the '⚡' bottom at the bottom to add one.",
            style = Typo.body2.style(
                color = Gray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )
    }
}