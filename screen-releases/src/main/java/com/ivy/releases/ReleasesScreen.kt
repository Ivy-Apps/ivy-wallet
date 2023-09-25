package com.ivy.releases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.navigation.navigation
import com.ivy.resources.R

@Composable
fun ReleasesScreenImpl() {
    val viewModel: ReleasesViewModel = viewModel()
    val uiState = viewModel.uiState()

    ReleasesUi(
        uiState = uiState,
        onEvent = {
            viewModel.onEvent(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReleasesUi(
    uiState: ReleasesState,
    onEvent: (ReleasesEvent) -> Unit
) {
    val browser = LocalUriHandler.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarTitle(title = "Releases")
                },
                navigationIcon = {
                    BackButton()
                }
            )
        },
        floatingActionButton = {
            GitHubButton {
                browser.openUri("https://github.com/Ivy-Apps/ivy-wallet")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = innerPadding,
        ) {
            content(
                releasesState = uiState,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun TopAppBarTitle(title: String) {
    Text(
        text = title,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun BackButton() {
    val nav = navigation()

    IconButton(onClick = {
        nav.back()
    }) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
}

private fun LazyListScope.content(
    releasesState: ReleasesState,
    onEvent: (ReleasesEvent) -> Unit
) {
    when (releasesState) {
        is ReleasesState.Error -> {
            item {
                ReleasesErrorState(
                    message = releasesState.errorMessage,
                    onClick = {
                        onEvent(ReleasesEvent.OnTryAgainClick)
                    }
                )
            }
        }

        is ReleasesState.Loading -> {
            item {
                Text(text = releasesState.loadingMessage)
            }
        }

        is ReleasesState.Success -> {
            items(releasesState.releasesInfo) {
                ReleaseInfoCard(releaseInfo = it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReleaseInfoCard(
    releaseInfo: ReleaseInfo,
    modifier: Modifier = Modifier
) {
    val browser = LocalUriHandler.current

    OutlinedCard(
        modifier = modifier,
        onClick = {
            browser.openUri(releaseInfo.releaseUrl)
        }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            ReleaseInfoRow(releaseInfo = releaseInfo)

            if (releaseInfo.releaseCommits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                for (commit in releaseInfo.releaseCommits) {
                    Text(text = "• $commit")
                }
            }
        }
    }
}

@Composable
private fun ReleaseInfoRow(
    releaseInfo: ReleaseInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReleaseName(info = releaseInfo.releaseName)
        Spacer(modifier = Modifier.weight(1f))
        ReleaseDate(info = releaseInfo.releaseDate)
    }
}

@Composable
private fun ReleaseName(
    info: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = info,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun ReleaseDate(
    info: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = info,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ReleasesErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        ElevatedButton(
            onClick = onClick
        ) {
            Text(text = "Try again")
        }
    }
}

@Composable
private fun GitHubButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            painter = painterResource(id = R.drawable.github_logo),
            contentDescription = "GitHub"
        )
    }
}