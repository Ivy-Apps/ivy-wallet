package com.ivy.wallet.ui.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R
import com.ivy.wallet.base.drawColoredShadow
import com.ivy.wallet.logic.model.CreateAccountData
import com.ivy.wallet.logic.model.CreateCategoryData
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.WrapContentRow

@Composable
fun Suggestions(
    suggestions: List<Any>,

    onAddSuggestion: (Any) -> Unit,
    onAddNew: () -> Unit
) {
    val items = suggestions.plus(AddNew())

    WrapContentRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        items = items,
        horizontalMarginBetweenItems = 8.dp,
        verticalMarginBetweenRows = 12.dp
    ) {
        when (it) {
            is CreateAccountData -> {
                Suggestion(name = it.name) {
                    onAddSuggestion(it)
                }
            }
            is CreateCategoryData -> {
                Suggestion(name = it.name) {
                    onAddSuggestion(it)
                }
            }
            is AddNew -> {
                AddNewButton {
                    onAddNew()
                }
            }
        }
    }
}

@Composable
private fun Suggestion(
    name: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(Shapes.roundedFull)
            .background(IvyTheme.colors.medium, Shapes.roundedFull)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        IvyIcon(icon = R.drawable.ic_plus)

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.padding(top = 14.dp, bottom = 18.dp),
            text = name,
            style = Typo.body2.style(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun AddNewButton(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .drawColoredShadow(color = IvyTheme.colors.mediumInverse)
            .clip(Shapes.roundedFull)
            .background(IvyTheme.colors.mediumInverse, Shapes.roundedFull)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        IvyIcon(
            icon = R.drawable.ic_plus,
            tint = IvyTheme.colors.pure,
        )

        Spacer(Modifier.width(8.dp))

        Text(
            modifier = Modifier.padding(top = 14.dp, bottom = 18.dp),
            text = "Add new",
            style = Typo.body2.style(
                color = IvyTheme.colors.pure,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.width(32.dp))
    }
}

private class AddNew


@Preview
@Composable
private fun Preview() {
    IvyComponentPreview {
        Suggestions(
            suggestions = listOf(
                Account("Cash"),
                Account("Bank"),
                Account("Revolut")
            ),
            onAddSuggestion = { }
        ) {

        }
    }
}