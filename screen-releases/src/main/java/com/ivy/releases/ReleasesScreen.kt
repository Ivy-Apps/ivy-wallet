package com.ivy.releases

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.navigation.Navigation
import com.ivy.navigation.navigation

@Composable
fun ReleasesScreenImpl() {
    val viewModel: ReleasesViewModel = viewModel()
    val uiState = viewModel.uiState()

    ReleasesUi(uiState = uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReleasesUi(
    uiState: ReleasesState
) {
    val nav = navigation()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarTitle(title = "Releases")
                },
                navigationIcon = {
                    BackButton(nav = nav)
                }
            )
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
            ) {
                content(releasesState = uiState)
            }
        }
    )
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
private fun BackButton(nav: Navigation) {
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
    releasesState: ReleasesState
) {
    when (releasesState) {
        is ReleasesState.Error -> item {
            Text(text = releasesState.errorMessage)
        }

        is ReleasesState.Loading -> item {
            Text(text = releasesState.loadingMessage)
        }

        is ReleasesState.Success -> items(releasesState.releasesInfo) {
            ReleasesInfoCard(releaseInfo = it)
        }
    }
}

@Composable
private fun ReleasesInfoCard(
    releaseInfo: ReleaseInfo,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row {
            Text(text = releaseInfo.releaseName)

            Spacer(modifier = Modifier.weight(1f))

            Text(text = releaseInfo.releaseDate)
        }

        for (commit in releaseInfo.releaseCommits) {
            Text(text = "• $commit")
        }
    }
}