package com.ivy.core.ui.account.pick

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivy.core.ui.data.account.AccountUi

@Composable
fun ChooseAccountLazyColumn(
    modifier: Modifier = Modifier,
    onChoose: (AccountUi) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
    ) {

    }
}