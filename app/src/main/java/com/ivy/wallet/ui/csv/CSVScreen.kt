package com.ivy.wallet.ui.csv

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.colorAs
import com.ivy.wallet.ui.ivyWalletCtx
import com.ivy.wallet.utils.thenIf

@Composable
fun CSVScreen() {
    val viewModel: CSVViewModel = viewModel()
    UI(state = viewModel.uiState(), onEvent = viewModel::onEvent)
}

@Composable
private fun UI(
    state: CSVState,
    onEvent: (CSVEvent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        contentPadding = PaddingValues(
            horizontal = 8.dp,
            vertical = 16.dp,
        )
    ) {
        item(key = "import_btn") {
            ImportButton(
                onFilePicked = {
                    onEvent(CSVEvent.FilePicked(it))
                }
            )
        }
        if (state.csv != null) {
            spacer8()
            csvTable(state.csv)
        }

    }
}

@Composable
private fun ImportButton(
    onFilePicked: (Uri) -> Unit,
) {
    val ivyContext = ivyWalletCtx()
    Button(
        onClick = {
            ivyContext.openFile {
                onFilePicked(it)
            }
        }
    ) {
        Text(text = "Import CSV")
    }
}

fun LazyListScope.spacer8() {
    item {
        Spacer8()
    }
}

@Composable
fun Spacer8() {
    Spacer(modifier = Modifier.height(8.dp))
}

private fun LazyListScope.csvTable(
    csv: List<CSVRow>
) {
    itemsIndexed(items = csv) { index, row ->
        CSVRow(row = row, header = index == 0, even = index % 2 == 0)
    }
}

@Composable
private fun CSVRow(
    row: CSVRow,
    header: Boolean,
    even: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        row.values.forEach { value ->
            CSVCell(text = value, header = header, even = even)
        }
    }
}

@Composable
private fun CSVCell(
    text: String,
    header: Boolean,
    even: Boolean
) {
    Text(
        modifier = Modifier
            .width(120.dp)
            .border(1.dp, UI.colors.pureInverse)
            .thenIf(even) {
                this.background(UI.colors.medium)
            }
            .padding(all = 4.dp),
        text = text,
        style = UI.typo.nB1,
        fontWeight = if (header) FontWeight.ExtraBold else FontWeight.Normal,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}


private fun <M> LazyListScope.mappingRow(
    columns: CSVRow,
    mapping: ColumnMapping<M>,
    onMapTo: (Int, String) -> Unit,
    metadataContent: (@Composable (M) -> Unit)? = null,
) {
    item {
        Spacer8()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = when {
                        mapping.required && !mapping.success -> UI.colors.red
                        mapping.success -> UI.colors.green
                        else -> UI.colors.medium
                    }
                )
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(
                text = mapping.ivyColumn,
                style = UI.typo.b1.colorAs(UI.colors.primary),
            )
            Spacer8()
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                columns.values.forEachIndexed { index, column ->
                    Button(
                        colors = if (column == mapping.name) {
                            ButtonDefaults.buttonColors()
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        },
                        onClick = {
                            onMapTo(index, column)
                        }
                    ) {
                        Text(text = column)
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }

            if (metadataContent != null) {
                Spacer8()
                metadataContent(mapping.metadata)
            }

            if (mapping.sampleValues.isNotEmpty()) {
                Spacer8()
                CSVRow(row = CSVRow(mapping.sampleValues), header = false, even = true)
            }
        }
        Spacer8()
    }
}

fun LazyListScope.sectionDivider(text: String) {
    item {
        Spacer8()
        Text(text = text, style = UI.typo.h2)
        Spacer8()
    }
}

fun LazyListScope.important(
    columns: CSVRow,
    importantFields: ImportantFields,
    onEvent: (CSVEvent) -> Unit
) {
    sectionDivider("Important")
    mappingRow(
        columns = columns,
        mapping = importantFields.amount,
        onMapTo = { index, name ->

        },
        metadataContent = {

        }
    )
}