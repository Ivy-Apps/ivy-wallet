package com.ivy.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.base.R
import com.ivy.base.data.AppBaseData
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.design.l0_system.UI
import com.ivy.frp.view.navigation.navigation
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.screens.Search
import com.ivy.wallet.ui.component.transaction.transactions
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.components.IvyBasicTextField
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.modal.DURATION_MODAL_ANIM
import com.ivy.wallet.utils.densityScope
import com.ivy.wallet.utils.keyboardOnlyWindowInsets
import com.ivy.wallet.utils.keyboardVisibleState
import com.ivy.wallet.utils.selectEndTextFieldValue

@Composable
fun SearchScreen(screen: Search) {
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
    transactions: List<Any>,
    baseCurrency: String,
    categories: List<CategoryOld>,
    accounts: List<AccountOld>,

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
            onSetSearchQueryTextField = {
                searchQueryTextFieldValue = it
                onSearch(it.text)
            }
        )

        LaunchedEffect(transactions) {
            //scroll to top when transactions are changed
            listState.animateScrollToItem(index = 0, scrollOffset = 0)
        }

        Spacer(Modifier.height(16.dp))

        val ivyContext = com.ivy.core.ui.temp.ivyWalletCtx()
        val nav = navigation()

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
                emptyStateTitle = com.ivy.core.ui.temp.stringRes(R.string.no_transactions),
                emptyStateText = com.ivy.core.ui.temp.stringRes(
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
                //add keyboard height margin at bototm so the list can scroll to bottom
            }
        }
    }
}

@Composable
private fun SearchInput(
    searchQueryTextFieldValue: TextFieldValue,
    onSetSearchQueryTextField: (TextFieldValue) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.rFull)
            .background(UI.colors.pure)
            .border(1.dp, Gray, UI.shapes.rFull),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(12.dp))

        IvyIcon(icon = R.drawable.ic_search)

        Spacer(Modifier.width(12.dp))

        val searchFocus = FocusRequester()
        IvyBasicTextField(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .focusRequester(searchFocus),
            value = searchQueryTextFieldValue,
            hint = stringResource(R.string.search_transactions),
            onValueChanged = {
                onSetSearchQueryTextField(it)
            }
        )

        onScreenStart {
            searchFocus.requestFocus()
        }

        Spacer(Modifier.weight(1f))

        IvyIcon(
            modifier = Modifier
                .clickable {
                    onSetSearchQueryTextField(selectEndTextFieldValue(""))
                }
                .padding(all = 12.dp), //enlarge click area
            icon = R.drawable.ic_outline_clear_24
        )

        Spacer(Modifier.width(8.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    com.ivy.core.ui.temp.Preview {
        UI(
            transactions = emptyList(),
            baseCurrency = "BGN",
            categories = emptyList(),
            accounts = emptyList()
        )
    }
}