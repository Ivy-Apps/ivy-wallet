package com.ivy.import_data.flow


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.IvyWalletPreview
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.GradientGreen
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.components.IvyDividerLine

@Composable
fun ImportProcessing(
    progressPercent: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(80.dp))

        Text(
            text = stringResource(R.string.please_wait),
            style = UI.typo.h2.style(
                fontWeight = FontWeight.Black
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${progressPercent}%",
            style = UI.typo.b2.style(
                color = Gray,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(24.dp))

        IvyDividerLine(
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.importing_the_csv_file),
            style = UI.typo.b2.style(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(16.dp))

        ProgressBar(
            progressPercent = progressPercent
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ProgressBar(
    progressPercent: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 24.dp)
            .background(UI.colors.medium, UI.shapes.rFull),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (progressPercent > 0) {
            Spacer(
                modifier = Modifier
                    .weight(progressPercent.toFloat())
                    .height(32.dp)
                    .background(GradientGreen.asHorizontalBrush(), UI.shapes.rFull),
            )
        }

        val uncompletedPercent = 100 - progressPercent
        if (uncompletedPercent > 0) {
            Spacer(
                modifier = Modifier
                    .weight(uncompletedPercent.toFloat())
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        ImportProcessing(
            progressPercent = 49
        )
    }
}