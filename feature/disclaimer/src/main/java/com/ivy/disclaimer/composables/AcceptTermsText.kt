package com.ivy.disclaimer.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AcceptTermsText(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = "Please read and agree to the following terms before using the app:",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
    )
}