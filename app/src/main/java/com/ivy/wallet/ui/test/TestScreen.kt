package com.ivy.wallet.ui.test

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.model.entity.User
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.components.ChooseIconModal
import com.ivy.wallet.ui.theme.components.IvyButton

@Composable
fun BoxWithConstraintsScope.TestScreen(screen: Screen.Test) {
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
        val ivyContext = LocalIvyContext.current

        if (user != null && user.testUser) {
            Spacer(Modifier.height(32.dp))

            IvyButton(text = "Analytics") {
                ivyContext.navigateTo(Screen.AnalyticsReport)
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