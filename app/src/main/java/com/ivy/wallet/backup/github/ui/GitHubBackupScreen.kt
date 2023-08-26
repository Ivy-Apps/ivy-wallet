package com.ivy.wallet.backup.github.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.frp.view.navigation.Screen
import com.ivy.frp.view.navigation.navigation

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
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        var repoUrl by rememberSaveable { mutableStateOf("") }
        var accessToken by rememberSaveable { mutableStateOf("") }

        OutlinedTextField(
            value = repoUrl,
            onValueChange = { repoUrl = it },
            label = { Text("GitHub repo url") },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = accessToken,
            onValueChange = { accessToken = it },
            label = { Text("GitHub PAT") }
        )
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedButton(
            onClick = {
                viewModel.enableBackups(repoUrl, accessToken)
            },
            enabled = repoUrl.isNotBlank() && accessToken.isNotBlank()
        ) {
            Text(text = "Connect")
        }
    }
}