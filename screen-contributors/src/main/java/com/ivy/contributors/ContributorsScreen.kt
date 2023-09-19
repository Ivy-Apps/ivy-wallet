package com.ivy.contributors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.navigation.Navigation
import com.ivy.navigation.navigation
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ContributorsScreenImpl() {
    val viewModel: ContributorsViewModel = viewModel()
    val uiState = viewModel.uiState()

    ContributorsUi(uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContributorsUi(
    uiState: ContributorsState
) {
    val nav = navigation()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarTitle(text = "Contributors")
                },
                navigationIcon = {
                    BackButton(nav = nav)
                }
            )
        },
        content = {
            ContributorsContent(paddingValues = it, contributors = uiState.contributors)
        }
    )
}

@Composable
private fun TopAppBarTitle(text: String) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = UI.typo.h2.style(
            fontWeight = FontWeight.Black
        )
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

@Composable
private fun ContributorsContent(
    paddingValues: PaddingValues,
    contributors: ImmutableList<Contributor>
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(contributors) { contributor ->
            ContributorCard(contributor = contributor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContributorCard(contributor: Contributor) {
    val browser = LocalUriHandler.current

    Card(
        shape = RoundedCornerShape(12.dp),
        onClick = {
            browser.openUri(contributor.link)
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = contributor.name
            )
        }
    }
}