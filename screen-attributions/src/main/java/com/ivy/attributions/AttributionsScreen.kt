package com.ivy.attributions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.design.l0_system.Gray
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.navigation.navigation

@Composable
fun AttributionsScreenImpl() {
    val viewModel: AttributionsViewModel = viewModel()
    val uiState = viewModel.uiState()

    AttributionsUI(uiState = uiState)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AttributionsUI(
    uiState: AttributionsState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("attributions_lazy_column")
    ) {
        stickyHeader {
            val nav = navigation()
            IvyToolbar(
                onBack = { nav.onBackPressed() },
            ) {
                Spacer(Modifier.weight(1f))

                val rootScreen = rootScreen()

                Spacer(Modifier.width(32.dp))
            }
        }

        item(key = "Attributions screen name") {
            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = "Attributions",
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )
        }

        item(key = "Icons section") {
            AttributionsSectionDivider(text = "Icons")

            Spacer(Modifier.height(16.dp))


        }
    }
}

@Composable
private fun AttributionCard() {

}

@Composable
private fun AttributionsSectionDivider(
    text: String,
    color: Color = Gray
) {
    Spacer(Modifier.height(32.dp))

    Text(
        modifier = Modifier.padding(start = 32.dp),
        text = text,
        style = UI.typo.b2.style(
            color = color,
            fontWeight = FontWeight.Bold
        )
    )
}