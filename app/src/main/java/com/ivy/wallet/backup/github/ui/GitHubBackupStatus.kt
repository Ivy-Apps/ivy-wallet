package com.ivy.wallet.backup.github.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.wallet.ui.theme.Orange

@Composable
fun GitHubBackupStatus(
    viewModel: GitHubBackupViewModel,
) {
    val status by viewModel.backupStatus.collectAsState()
    if (status == null) return


    when (val stat = status) {
        is GitHubBackupStatus.Error -> {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier,
                text = stat.error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Start,
            )
        }

        GitHubBackupStatus.Loading -> {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier,
                    color = Orange,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    modifier = Modifier,
                    text = "Backing up...",
                    color = Orange,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        GitHubBackupStatus.Success -> {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier,
                text = "Hurray! Backup successful!",
                style = MaterialTheme.typography.bodyMedium,
                color = UI.colors.green,
                textAlign = TextAlign.Start,
            )
        }

        null -> {}
    }
}