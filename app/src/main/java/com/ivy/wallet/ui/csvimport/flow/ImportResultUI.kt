package com.ivy.wallet.ui.csvimport.flow

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.design.api.navigation
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.base.format
import com.ivy.wallet.logic.csv.model.ImportResult
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.BackButton
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.ui.theme.components.OnboardingButton

@Composable
fun ImportResultUI(
    result: ImportResult,

    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Spacer(Modifier.height(16.dp))

        val nav = navigation()
        BackButton(
            modifier = Modifier.padding(start = 20.dp)
        ) {
            nav.onBackPressed()
        }

        Spacer(Modifier.height(24.dp))

        val importSuccess = result.transactionsImported > 0 &&
                result.transactionsImported > result.rowsFound / 2
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = if (importSuccess) "Success" else "Failure",
            style = UI.typo.h2.style(
                fontWeight = FontWeight.Black,
                color = if (importSuccess) IvyTheme.colors.pureInverse else Red
            )
        )

        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "Imported",
            style = UI.typo.b1.style(
                color = Green,
                fontWeight = FontWeight.Black
            )
        )

        Spacer(Modifier.height(8.dp))

        val successPercent = if (result.rowsFound > 0)
            (result.transactionsImported / result.rowsFound.toDouble()) * 100 else 0.0
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "${successPercent.format(2)}%",
            style = UI.typo.nH2.style(
                fontWeight = FontWeight.Normal
            )
        )

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "${result.transactionsImported} transactions",
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Bold,
                color = Gray
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "${result.accountsImported} accounts",
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Bold,
                color = Gray
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "${result.categoriesImported} categories",
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Bold,
                color = Gray
            )
        )


        Spacer(Modifier.height(32.dp))

        IvyDividerLine(
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(32.dp))


        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "Failed",
            style = UI.typo.b1.style(
                fontWeight = FontWeight.Black,
                color = Red
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "${(100 - successPercent).format(2)}%",
            style = UI.typo.nH2.style(
                fontWeight = FontWeight.Normal
            )
        )

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "${result.rowsFound - result.transactionsImported} rows from CSV file not recognized",
            style = UI.typo.nB2.style(
                fontWeight = FontWeight.Bold,
                color = Gray
            )
        )

        //TODO: Implement "See failed imports"

        Spacer(Modifier.weight(1f))

        OnboardingButton(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = "Finish",
            textColor = White,
            backgroundGradient = GradientIvy,
            hasNext = true,
            enabled = true
        ) {
            onFinish()
        }

        Spacer(Modifier.height(16.dp))
    }
}


@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        ImportResultUI(
            result = ImportResult(
                rowsFound = 356,
                transactionsImported = 320,
                accountsImported = 4,
                categoriesImported = 13,
                failedRows = emptyList()
            )
        ) {

        }
    }
}