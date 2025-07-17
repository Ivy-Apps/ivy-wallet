package com.ivy.poll.impl.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivy.design.system.colors.IvyColors

@Composable
fun VotedContent(
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(all = 16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Icon(
      modifier = Modifier.size(48.dp),
      imageVector = Icons.Filled.Done,
      tint = IvyColors.Green.primary,
      contentDescription = null,
    )
    Spacer(Modifier.height(16.dp))
    Text(
      text = "Thanks for voting!",
      style = MaterialTheme.typography.headlineSmall,
    )
    Spacer(Modifier.height(48.dp))
    Button(
      onClick = onBackClick,
    ) {
      Text("Go back")
    }
  }
}