package com.ivy.design.l2_components.modal.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l2_components.input.InputFieldType
import com.ivy.design.l2_components.input.IvyInputField
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l2_components.modal.scope.ModalActionsScope
import com.ivy.design.l2_components.modal.scope.ModalScope
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.util.IvyPreview
import com.ivy.resources.R


@Composable
fun ModalScope.Search(
    searchBarVisible: Boolean,
    initialSearchQuery: String,
    searchHint: String,
    resetSearch: () -> Unit,
    onSearch: (String) -> Unit,
    overlay: (@Composable BoxScope.() -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    Box(modifier = Modifier.weight(1f)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
        SearchBar(
            visible = searchBarVisible,
            initialQuery = initialSearchQuery,
            hint = searchHint,
            resetSearch = resetSearch,
            onSearch = onSearch
        )
        overlay?.invoke(this)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    visible: Boolean,
    initialQuery: String,
    hint: String,
    resetSearch: () -> Unit,
    onSearch: (String) -> Unit,
) {
    // the FocusRequester must be remembered outside AnimatedVisibility
    // otherwise it crashes for no reason
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxWidth()
            .background(UI.colors.pure)
            .padding(top = 16.dp, bottom = 8.dp),
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        IvyInputField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            type = InputFieldType.SingleLine,
            initialValue = initialQuery,
            placeholder = hint,
            iconLeft = R.drawable.round_search_24,
            imeAction = ImeAction.Search,
            onImeAction = {
                keyboardController?.hide()
                focusRequester.freeFocus()
            },
            onValueChange = { onSearch(it) },
        )

        LaunchedEffect(visible) {
            if (visible) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        }
        BackHandler(enabled = visible) {
            resetSearch()
        }
    }
}

@Composable
fun ModalActionsScope.SearchButton(
    searchBarVisible: Boolean,
    onClick: () -> Unit,
) {
    Secondary(
        text = null,
        icon = if (searchBarVisible)
            R.drawable.round_search_off_24 else R.drawable.round_search_24,
        feeling = if (searchBarVisible) Feeling.Negative else Feeling.Positive,
        onClick = onClick,
    )
}


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        Modal(
            modal = modal,
            actions = {
                SearchButton(
                    searchBarVisible = true,
                ) {

                }
            }
        ) {
            Search(
                searchBarVisible = true,
                initialSearchQuery = "",
                searchHint = "Search hint",
                resetSearch = { },
                onSearch = {}
            ) {
                item {
                    Title("Modal title")
                }
            }
        }
    }
}
// endregion