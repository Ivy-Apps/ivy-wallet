package com.ivy.wallet.ui.test

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.wallet.base.format
import com.ivy.wallet.base.onScreenStart
import com.ivy.wallet.model.entity.User
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.LocalIvyContext
import com.ivy.wallet.ui.Screen
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.components.ChooseIconModal
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.charts.IvyLineChart
import com.ivy.wallet.ui.theme.components.charts.Value
import com.ivy.wallet.ui.theme.modal.model.Month
import timber.log.Timber

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

        val values = listOf(
            Value(
                x = 0.0,
                y = 5235.60
            ),
            Value(
                x = 1.0,
                y = 8000.0
            ),
            Value(
                x = 2.0,
                y = 15032.89
            ),
            Value(
                x = 3.0,
                y = 4123.0
            ),
            Value(
                x = 4.0,
                y = 1000.0
            ),
            Value(
                x = 5.0,
                y = -5000.0
            ),
            Value(
                x = 6.0,
                y = 3000.0
            ),
            Value(
                x = 7.0,
                y = 9000.0
            ),
            Value(
                x = 8.0,
                y = 15600.50
            ),
            Value(
                x = 9.0,
                y = 20000.0
            ),
            Value(
                x = 10.0,
                y = 0.0
            ),
            Value(
                x = 11.0,
                y = 1000.0
            ),
        )

        IvyLineChart(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            values = values,
            xLabel = {
                Month.monthsList()[it.toInt()].name.first().toString()
            },
            yLabel = {
                it.format("BGN")
            },
            onTap = {
                Timber.i("CHART onTap: index = $it")
            }
        )

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