package com.ivy.disclaimer.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.disclaimer.CheckboxViewState

@Composable
fun AgreementCheckBox(
    viewState: CheckboxViewState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = viewState.checked, onCheckedChange = { onClick() })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = viewState.text)
    }
}