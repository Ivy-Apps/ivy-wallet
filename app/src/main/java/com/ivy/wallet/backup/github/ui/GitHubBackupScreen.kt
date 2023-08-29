package com.ivy.wallet.backup.github.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.frp.view.navigation.Screen
import com.ivy.frp.view.navigation.navigation

private const val GITHUB_REPO_INFO_URL =
    "https://docs.github.com/en/get-started/quickstart/create-a-repo"

private const val GITHUB_PAT_INFO_URL =
    "https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-fine-grained-personal-access-token"

private const val VIDEO_TUTORIAL_URL =
    "https://www.youtube.com/watch?v=wcgORjVFy4I"

object GitHubBackupScreen : Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitHubBackupScreen() {
    val nav = navigation()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "GitHub Backups",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        nav.back()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        content = { innerPadding ->
            Content(
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<GitHubBackupViewModel>()

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        var repoUrl by rememberSaveable { mutableStateOf("") }
        var gitHubPAT by rememberSaveable { mutableStateOf("") }

        LaunchedEffect(Unit) {
            viewModel.getCredentials()?.let {
                repoUrl = it.repoUrl
                gitHubPAT = it.gitHubPAT
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        HeaderInfo()

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = repoUrl,
                onValueChange = { repoUrl = it },
                label = { Text("GitHub repo url") },
            )
            Spacer(modifier = Modifier.width(12.dp))
            InfoButton(infoUrl = GITHUB_REPO_INFO_URL)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = gitHubPAT,
                onValueChange = { gitHubPAT = it },
                label = { Text("GitHub PAT") }
            )
            Spacer(modifier = Modifier.width(12.dp))
            InfoButton(infoUrl = GITHUB_PAT_INFO_URL)
        }

        Spacer(modifier = Modifier.height(8.dp))
        val uriHandler = LocalUriHandler.current
        TextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                uriHandler.openUri(VIDEO_TUTORIAL_URL)
            }
        ) {
            Text(text = "Need help? Watch our video tutorial")
        }

        Spacer(modifier = Modifier.height(12.dp))
        val enabled by viewModel.enabled.collectAsState(initial = false)
        ElevatedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.enableBackups(repoUrl, gitHubPAT)
            },
            enabled = repoUrl.isNotBlank() && gitHubPAT.isNotBlank()
        ) {
            Text(text = if (!enabled) "Enable backups" else "Update connection")
        }
        if (enabled) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = viewModel::backupData
                ) {
                    Text("Backup now")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = viewModel::importFromGitHub
                ) {
                    Text("Import from GitHub")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            GitHubBackupStatus(viewModel = viewModel)
        }

        // Scroll fix
        Spacer(modifier = Modifier.height(320.dp))
    }
}

@Composable
private fun InfoButton(
    infoUrl: String,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    FilledIconButton(
        modifier = modifier,
        onClick = {
            uriHandler.openUri(infoUrl)
        }
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "Info"
        )
    }
}

@Composable
private fun ColumnScope.HeaderInfo() {
    Text(
        modifier = Modifier,
        text = "Ivy Wallet will try to perform an automatic backup of your data every day around 12 PM.",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Start,
    )
}