package com.ivy.wallet.ui.theme.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.IvyCurrency
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.modal.DURATION_MODAL_ANIM
import com.ivy.wallet.utils.*
import java.util.*

@Composable
fun CurrencyPicker(
    modifier: Modifier = Modifier,
    initialSelectedCurrency: IvyCurrency?,
    preselectedCurrency: IvyCurrency = IvyCurrency.getDefault(),

    includeKeyboardShownInsetSpacer: Boolean,
    lastItemSpacer: Dp = 0.dp,

    onKeyboardShown: (keyboardVisible: Boolean) -> Unit = {},

    onSelectedCurrencyChanged: (IvyCurrency) -> Unit
) {
    val rootView = LocalView.current
    var keyboardShown by remember { mutableStateOf(false) }

    onScreenStart {
        rootView.addKeyboardListener {
            keyboardShown = it
            onKeyboardShown(it)
        }
    }

    val keyboardShownInsetDp by animateDpAsState(
        targetValue = densityScope {
            if (keyboardShown) keyboardOnlyWindowInsets().bottom.toDp() else 0.dp
        },
        animationSpec = tween(DURATION_MODAL_ANIM)
    )

    Column(
        modifier = modifier
    ) {
        var preselected by remember {
            mutableStateOf(initialSelectedCurrency == null)
        }
        var selectedCurrency by remember {
            mutableStateOf(initialSelectedCurrency ?: preselectedCurrency)
        }

        var searchTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }

        if (!keyboardShown) {
            SelectedCurrencyCard(
                currency = selectedCurrency,
                preselected = preselected
            )

            Spacer(Modifier.height(20.dp))
        }

        SearchInput(searchTextFieldValue = searchTextFieldValue) {
            searchTextFieldValue = it
        }

        Spacer(Modifier.height(20.dp))

        CurrencyList(
            searchQueryLowercase = searchTextFieldValue.text.toLowerCase(Locale.getDefault()),
            selectedCurrency = selectedCurrency,
            lastItemSpacer = if (includeKeyboardShownInsetSpacer)
                keyboardShownInsetDp + lastItemSpacer else lastItemSpacer,
        ) {
            preselected = false
            selectedCurrency = it
            onSelectedCurrencyChanged(it)
        }
    }
}

@Composable
private fun SearchInput(
    searchTextFieldValue: TextFieldValue,
    onSetSearchTextFieldValue: (TextFieldValue) -> Unit
) {
    val inputFocus = FocusRequester()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.rFull)
            .border(2.dp, UI.colors.mediumInverse, UI.shapes.rFull)
            .clickable {
                inputFocus.requestFocus()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(Modifier.width(16.dp))

        IvyIcon(
            modifier = Modifier.padding(vertical = 8.dp),
            icon = R.drawable.ic_search
        )

        Spacer(Modifier.width(8.dp))

        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            if (searchTextFieldValue.text.isEmpty()) {
                //Hint
                Text(
                    text = stringResource(R.string.search_currency),
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            val view = LocalView.current
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .focusRequester(inputFocus)
                    .testTag("search_input"),
                value = searchTextFieldValue,
                onValueChange = {
                    onSetSearchTextFieldValue(it.copy(it.text.trim()))
                },
                textStyle = UI.typo.b2.style(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                ),
                singleLine = true,
                cursorBrush = SolidColor(UI.colors.pureInverse),
                keyboardActions = KeyboardActions(
                    onDone = {
                        hideKeyboard(view)
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
            )
        }
    }
}

@Composable
private fun SelectedCurrencyCard(
    currency: IvyCurrency,
    preselected: Boolean,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r3)
            .background(
                brush = (if (preselected) GradientGreen else GradientIvy).asHorizontalBrush(),
                shape = UI.shapes.r3
            )
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        Column {
            Text(
                text = currency.name,
                style = UI.typo.b2.style(
                    color = White,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = currency.code,
                style = UI.typo.b1.style(
                    color = White,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Spacer(Modifier.weight(1f))

        IvyIcon(
            icon = R.drawable.ic_check,
            tint = White
        )

        Text(
            text = if (preselected) stringResource(R.string.pre_selected) else stringResource(R.string.selected),
            style = UI.typo.b2.style(
                color = White,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun CurrencyList(
    searchQueryLowercase: String,
    selectedCurrency: IvyCurrency,
    lastItemSpacer: Dp,
    onCurrencySelected: (IvyCurrency) -> Unit
) {
    val currencies = IvyCurrency.getAvailable()
        .filter {
            searchQueryLowercase.isBlank() ||
                    it.code.toLowerCaseLocal().startsWith(searchQueryLowercase) ||
                    it.name.toLowerCaseLocal().startsWith(searchQueryLowercase)
        }
        .sortedBy { it.code }
        .sortedBy { it.isCrypto }

    val currenciesWithLetters = mutableListOf<Any>()

    var lastFirstLetter: String? = null
    for (currency in currencies) {
        val firstLetter =
            if (currency.isCrypto) stringResource(R.string.crypto) else currency.code.first()
                .toString()
        if (firstLetter != lastFirstLetter) {
            currenciesWithLetters.add(
                LetterDivider(
                    letter = firstLetter
                )
            )
            lastFirstLetter = firstLetter
        }

        currenciesWithLetters.add(currency)
    }

    val listState = remember(searchQueryLowercase, selectedCurrency) {
        LazyListState(
            firstVisibleItemIndex = 0,
            firstVisibleItemScrollOffset = 0
        )
    }


    LazyColumn(
        state = listState
    ) {
        itemsIndexed(currenciesWithLetters) { index, item ->
            when (item) {
                is IvyCurrency -> {
                    CurrencyItemCard(
                        currency = item,
                        selected = item == selectedCurrency
                    ) {
                        onCurrencySelected(item)
                    }
                }
                is LetterDivider -> {
                    LetterDividerItem(
                        spacerTop = if (index == 0) 12.dp else 32.dp,
                        letterDivider = item
                    )
                }
            }
        }

        if (lastItemSpacer.value > 0) {
            item {
                Spacer(Modifier.height(lastItemSpacer))
            }
        }
    }
}

@Composable
private fun CurrencyItemCard(
    currency: IvyCurrency,
    selected: Boolean,

    onClick: () -> Unit,
) {
    Spacer(Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .background(
                color = if (selected) Ivy else UI.colors.medium,
                shape = UI.shapes.r4
            )
            .clickable {
                onClick()
            }
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        Text(
            text = currency.code,
            style = UI.typo.b1.style(
                color = if (selected) White else UI.colors.pureInverse,
                fontWeight = FontWeight.ExtraBold
            )
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = currency.name.take(20),
            style = UI.typo.b2.style(
                color = if (selected) White else UI.colors.pureInverse,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun LetterDividerItem(
    spacerTop: Dp,
    letterDivider: LetterDivider
) {
    if (spacerTop > 0.dp) {
        Spacer(Modifier.height(spacerTop))
    }

    Text(
        modifier = Modifier.padding(start = 32.dp),
        text = letterDivider.letter,
        style = UI.typo.c.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.SemiBold
        )
    )

    Spacer(Modifier.height(6.dp))
}

@Preview
@Composable
private fun Preview() {
    com.ivy.core.ui.temp.ComponentPreview {
        CurrencyPicker(
            initialSelectedCurrency = null,
            includeKeyboardShownInsetSpacer = true
        ) {

        }
    }
}

private data class LetterDivider(
    val letter: String
)