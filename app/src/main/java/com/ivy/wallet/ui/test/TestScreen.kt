package com.ivy.wallet.ui.test

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.design.api.navigation
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.model.entity.User
import com.ivy.wallet.ui.AnalyticsReport
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.Test
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.modal.ChooseIconModal

@Composable
fun BoxWithConstraintsScope.TestScreen(screen: Test) {
    val viewModel: TestViewModel = viewModel()

    val user by viewModel.user.observeAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        user = user,

        onSyncCategories = viewModel::syncCategories,
        onTestWorker = viewModel::testWorker
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    user: User?,

    onSyncCategories: () -> Unit,
    onTestWorker: () -> Unit
) {
    var chooseIconModalVisible by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val nav = navigation()

        if (user != null && user.testUser) {
            Spacer(Modifier.height(32.dp))

            IvyButton(text = "Analytics") {
                nav.navigateTo(AnalyticsReport)
            }
        }
    }

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
    IvyAppPreview {
        UI(
            user = null,

            onSyncCategories = {},
            onTestWorker = {}
        )
    }
}