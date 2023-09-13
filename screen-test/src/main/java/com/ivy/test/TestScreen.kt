package com.ivy.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.legacy.IvyWalletPreview
import com.ivy.navigation.Test
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.modal.ChooseIconModal

@Composable
fun BoxWithConstraintsScope.TestScreen(screen: Test) {
    val viewModel: TestViewModel = viewModel()

    com.ivy.legacy.utils.onScreenStart {
        viewModel.start()
    }

    UI()
}

@Composable
private fun BoxWithConstraintsScope.UI(
) {
    var chooseIconModalVisible by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {}

    ChooseIconModal(
        visible = chooseIconModalVisible,
        initialIcon = null,
        color = Ivy,
        dismiss = { chooseIconModalVisible = false },
        onIconChosen = {}
    )
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        UI()
    }
}
