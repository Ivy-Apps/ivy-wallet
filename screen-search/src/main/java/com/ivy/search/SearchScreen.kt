package com.ivy.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.core.data.model.Account
import com.ivy.core.data.model.Category
import com.ivy.core.data.model.TransactionHistoryItem
import com.ivy.core.utils.stringRes
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.data.AppBaseData
import com.ivy.legacy.ui.SearchInput
import com.ivy.legacy.ui.component.transaction.transactions
import com.ivy.legacy.utils.densityScope
import com.ivy.legacy.utils.keyboardOnlyWindowInsets
import com.ivy.legacy.utils.keyboardVisibleState
import com.ivy.legacy.utils.onScreenStart
import com.ivy.legacy.utils.selectEndTextFieldValue
import com.ivy.navigation.SearchScreen
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.modal.DURATION_MODAL_ANIM
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SearchScreen(screen: SearchScreen) {
    val viewModel: SearchViewModel = viewModel()

    val transactions by viewModel.transactions.collectAsState()
    val baseCurrency by viewModel.baseCurrencyCode.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    onScreenStart {
        viewModel.search("")
    }

    UI(
        transactions = transactions,
        baseCurrency = baseCurrency,
        categories = categories,
        accounts = accounts,

        onSearch = viewModel::search
    )
}

@Composable
private fun UI(
    transactions: ImmutableList<TransactionHistoryItem>,
    baseCurrency: String,
    categories: ImmutableList<Category>,
    accounts: ImmutableList<Account>,

    onSearch: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Spacer(Modifier.height(24.dp))

        val listState = rememberLazyListState()

        var searchQueryTextFieldValue by remember {
            mutableStateOf(selectEndTextFieldValue(""))
        }

        SearchInput(
            searchQueryTextFieldValue = searchQueryTextFieldValue,
            hint = stringResource(R.string.search_transactions),
            onSetSearchQueryTextField = {
                searchQueryTextFieldValue = it
                onSearch(it.text)
            }
        )

        LaunchedEffect(transactions) {
            // scroll to top when transactions are changed
            listState.animateScrollToItem(index = 0, scrollOffset = 0)
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState

        ) {
            transactions(
                baseData = AppBaseData(
                    baseCurrency = baseCurrency,
                    accounts = accounts,
                    categories = categories
                ),
                upcoming = null,
                setUpcomingExpanded = { },
                overdue = null,
                setOverdueExpanded = { },
                history = transactions,
                onPayOrGet = { },
                emptyStateTitle = stringRes(R.string.no_transactions),
                emptyStateText = stringRes(
                    R.string.no_transactions_for_query,
                    searchQueryTextFieldValue.text
                ),
                dateDividerMarginTop = 16.dp
            )

            item {
                val keyboardVisible by keyboardVisibleState()
                val keyboardShownInsetDp by animateDpAsState(
                    targetValue = densityScope {
                        if (keyboardVisible) keyboardOnlyWindowInsets().bottom.toDp() else 0.dp
                    },
                    animationSpec = tween(DURATION_MODAL_ANIM)
                )

                Spacer(Modifier.height(keyboardShownInsetDp))
                // add keyboard height margin at bototm so the list can scroll to bottom
            }
        }
    }
}


@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI(
            transactions = persistentListOf(),
            baseCurrency = "BGN",
            categories = persistentListOf(),
            accounts = persistentListOf()
        )
    }
}
