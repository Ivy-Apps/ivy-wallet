package com.ivy.importdata.csvimport.flow

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.navigation.navigation
import com.ivy.legacy.domain.deprecated.logic.csv.model.ImportType
import com.ivy.navigation.CSVScreen
import com.ivy.onboarding.components.OnboardingToolbar
import com.ivy.resources.R
import com.ivy.wallet.ui.theme.components.GradientCutBottom
import com.ivy.wallet.ui.theme.components.IvyIcon

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ImportFrom(
    hasSkip: Boolean,
    launchedFromOnboarding: Boolean,

    onSkip: () -> Unit = {},
    onImportFrom: (ImportType) -> Unit = {},
) {
    val importTypes = ImportType.values()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        stickyHeader {
            val nav = navigation()
            OnboardingToolbar(
                hasSkip = hasSkip,
                onBack = { nav.onBackPressed() },
                onSkip = onSkip
            )
            // onboarding toolbar include paddingBottom 16.dp
        }

        item {
            Spacer(Modifier.height(8.dp))
            val nav = navigation()
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                onClick = {
                    nav.navigateTo(CSVScreen(launchedFromOnboarding))
                }
            ) {
                Text(text = "Manual CSV import")
            }
        }

        item {
            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = stringResource(R.string.import_from),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(Modifier.height(24.dp))
        }

        items(importTypes) {
            ImportOption(
                importType = it,
                onImportFrom = onImportFrom
            )
        }

        item {
            // last spacer
            Spacer(Modifier.height(96.dp))
        }
    }

    GradientCutBottom(
        height = 96.dp
    )
}

@Composable
private fun ImportOption(
    importType: ImportType,
    onImportFrom: (ImportType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r3)
            .background(UI.colors.medium, UI.shapes.r3)
            .clickable {
                onImportFrom(importType)
            }
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(20.dp))

        IvyIcon(
            modifier = Modifier.size(32.dp),
            icon = importType.logo(),
            tint = Color.Unspecified
        )

        Text(
            modifier = Modifier.padding(start = 16.dp, end = 32.dp),
            text = importType.listName(),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold,
                color = UI.colors.pureInverse
            )
        )
    }

    Spacer(Modifier.height(8.dp))
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    com.ivy.legacy.IvyWalletPreview {
        ImportFrom(
            hasSkip = true,
            launchedFromOnboarding = false,
        )
    }
}
