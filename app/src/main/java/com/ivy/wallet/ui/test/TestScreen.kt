package com.ivy.wallet.ui.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.domain.data.core.User
import com.ivy.wallet.ui.AnalyticsReport
import com.ivy.wallet.ui.IvyWalletPreview
import com.ivy.wallet.ui.Test
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.modal.ChooseIconModal
import com.ivy.wallet.utils.onScreenStart

@Composable
fun BoxWithConstraintsScope.TestScreen(screen: Test) {
    val viewModel: TestViewModel = viewModel()

    val user by viewModel.user.observeAsState()

    onScreenStart {
        viewModel.start()
    }

    UI(
        user = user,
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    user: User?,
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
    IvyWalletPreview {
        UI(
            user = null,
        )
    }
}
