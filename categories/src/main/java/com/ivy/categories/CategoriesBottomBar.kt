package com.ivy.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.base.IvyWalletPreview
import com.ivy.wallet.ui.theme.Blue
import com.ivy.wallet.ui.theme.components.BackBottomBar
import com.ivy.wallet.ui.theme.components.IvyButton

@Composable
internal fun BoxWithConstraintsScope.CategoriesBottomBar(
    onClose: () -> Unit,
    onAddCategory: () -> Unit
) {
    BackBottomBar(onBack = onClose) {
        IvyButton(
            text = stringResource(R.string.add_category),
            iconStart = R.drawable.ic_plus
        ) {
            onAddCategory()
        }
    }
}

@Preview
@Composable
private fun PreviewBottomBar() {
    IvyWalletPreview {
        Column(
            Modifier
                .fillMaxSize()
                .background(Blue)
        ) {

        }

        CategoriesBottomBar(
            onAddCategory = {},
            onClose = {}
        )
    }
}
