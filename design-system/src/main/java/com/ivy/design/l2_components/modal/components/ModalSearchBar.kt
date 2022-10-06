package com.ivy.design.l2_components.modal.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.ivy.design.util.IvyPreview

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ModalSearchBar(
    visible: Boolean,
    query: String,
    hint: String,
    resetSearch: () -> Unit,
    onSearch: (String) -> Unit,
) {
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxWidth()
            .background(UI.colors.pure)
            .padding(top = 16.dp, bottom = 8.dp),
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        IvyInputField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            type = InputFieldType.SingleLine,
            initialValue = query,
            placeholder = hint,
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


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        ModalSearchBar(
            visible = true,
            query = "",
            hint = "Search by words (car, home, tech)",
            resetSearch = {},
            onSearch = {}
        )
    }
}
// endregion