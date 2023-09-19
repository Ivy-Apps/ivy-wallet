package com.ivy.attributions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.design.l0_system.Black
import com.ivy.design.l0_system.Gray
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.IvyWalletPreview
import com.ivy.navigation.Navigation
import com.ivy.navigation.navigation

@Composable
fun AttributionsScreenImpl() {
    val viewModel: AttributionsViewModel = viewModel()
    val uiState = viewModel.uiState()

    AttributionsUI(uiState = uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributionsUI(
    uiState: AttributionsState
) {
    val nav = navigation()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarTitle(text = "Attributions")
                },
                navigationIcon = {
                    BackButton(nav = nav)
                }
            )
        },
        content = {
            AttributionsContent(paddingValues = it, attributionItems = uiState.attributionItems)
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
            contentDescription = "Back",
            tint = Black
        )
    }
}

@Composable
private fun AttributionsContent(
    paddingValues: PaddingValues,
    attributionItems: List<AttributionItem>
) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(attributionItems) { attributionItem ->
            AttributionLayout(attributionItem)
        }
    }
}

@Composable
private fun AttributionLayout(
    attributionItem: AttributionItem
) {
    when (attributionItem) {
        is AttributionItem.Attribution -> AttributionCard(attribution = attributionItem)
        is AttributionItem.Divider -> AttributionsSectionDivider(text = attributionItem.sectionName)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributionCard(attribution: AttributionItem.Attribution) {
    val browser = LocalUriHandler.current

    Card(
        shape = RoundedCornerShape(12.dp),
        onClick = {
            browser.openUri(attribution.link)
        }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = attribution.name
            )
        }
    }
}

@Composable
private fun AttributionsSectionDivider(
    text: String,
    color: Color = Gray
) {
    Spacer(modifier = Modifier.height(12.dp))

    Text(
        modifier = Modifier.padding(start = 12.dp),
        text = text,
        style = UI.typo.b2.style(
            color = color,
            fontWeight = FontWeight.Bold
        )
    )
}

@Preview
@Composable
private fun AttributionsUIPreview() {
    val attributionItems = listOf<AttributionItem>(
        AttributionItem.Divider(sectionName = "Icons"),
        AttributionItem.Attribution(name = "Icon1", link = "https://www.google.com"),
        AttributionItem.Attribution(name = "Icon2", link = "https://www.google.com"),
        AttributionItem.Divider(sectionName = "Typography"),
        AttributionItem.Attribution(name = "Typography1", link = "https://www.google.com"),
    )

    IvyWalletPreview {
        AttributionsUI(uiState = AttributionsState(attributionItems))
    }
}