package com.ivy.wallet.backup.github.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.design.l0_system.UI
import com.ivy.frp.view.navigation.navigation
import com.ivy.wallet.R
import com.ivy.wallet.ui.theme.Orange

@Composable
fun GitHubBackupCard(
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<GitHubBackupViewModel>()
    val enabled by viewModel.enabled.collectAsState(initial = false)

    if (enabled) {
        BackupEnabled(
            modifier = modifier,
            viewModel = viewModel
        )
    } else {
        BackupDisabled(
            modifier = modifier,
        )
    }
}

@Composable
private fun BackupEnabled(
    viewModel: GitHubBackupViewModel,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = UI.colors.medium,
            contentColor = UI.colors.mediumInverse,
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                modifier = Modifier,
                text = "GitHub auto-backups",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            )
            GitHubBackupStatus(viewModel)
            LastBackup(viewModel)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ElevatedButton(
                    onClick = viewModel::backupData
                ) {
                    Text(text = "Backup now")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    onClick = viewModel::disableBackups,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                ) {
                    Text("Disable")
                }
            }
        }
    }
}

@Composable
private fun GitHubBackupStatus(
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

@Composable
private fun LastBackup(
    viewModel: GitHubBackupViewModel,
) {
    val lastBackupTime by viewModel.lastBackupTime.collectAsState(initial = null)
    if (lastBackupTime != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier,
            text = "Last backup at $lastBackupTime",
            color = UI.colors.gray,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(8.dp))
        val uriHandler = LocalUriHandler.current
        OutlinedButton(
            onClick = {
                viewModel.viewBackup(uriHandler::openUri)
            }
        ) {
            Text("View backup")
        }
    }
}

@Composable
private fun BackupDisabled(
    modifier: Modifier = Modifier,
) {
    val nav = navigation()
    Button(
        modifier = modifier,
        onClick = {
            nav.navigateTo(GitHubBackupScreen)
        },
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.github_logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(LocalContentColor.current)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text("Enable GitHub auto backups")
        Spacer(modifier = Modifier.weight(1f))
    }
}