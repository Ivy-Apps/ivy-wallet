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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.ivy.wallet.ui.theme.White

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackupEnabled(
    viewModel: GitHubBackupViewModel,
    modifier: Modifier = Modifier,
) {
    val nav = navigation()
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = UI.colors.medium,
            contentColor = UI.colors.mediumInverse,
        ),
        onClick = {
            nav.navigateTo(GitHubBackupScreen)
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                GitHubIcon()
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    modifier = Modifier,
                    text = "GitHub auto-backups",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier,
                text = "Ivy Wallet will perform an automatic backup of your data every day at 12:00 PM.",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Normal,
            )
            LastBackup(viewModel)
            GitHubBackupStatus(viewModel)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = viewModel::backupData,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UI.colors.green,
                        contentColor = White,
                    )
                ) {
                    Text("Backup now")
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = viewModel::disableBackups,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Last Backup: $lastBackupTime",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.width(12.dp))
            val uriHandler = LocalUriHandler.current
            TextButton(
                onClick = {
                    viewModel.viewBackup(uriHandler::openUri)
                }
            ) {
                Text("View")
            }
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
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        GitHubIcon()
        Spacer(modifier = Modifier.width(16.dp))
        Text("Enable GitHub auto-backups")
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun GitHubIcon(
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = R.drawable.github_logo),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(LocalContentColor.current)
    )
}