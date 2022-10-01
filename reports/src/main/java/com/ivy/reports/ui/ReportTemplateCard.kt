package com.ivy.reports.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.ivy.core.functions.icon.dummyIconSized
import com.ivy.core.ui.component.BadgeComponent
import com.ivy.design.l0_system.OrangeLight
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.reports.R
import com.ivy.reports.template.data.TemplateLabels
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.RedLight

@Composable
fun ReportTemplateCard() {
    Column(
        modifier = Modifier
            .padding(all = 20.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .background(UI.colors.medium, UI.shapes.r4)
            .clickable(onClick = {
                //Todo
            })
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp)
    ) {
        Title(title = "Dummy Title")
        SpacerVer(height = 8.dp)
        AccountsAndCategory(17, 17)
        SpacerVer(height = 8.dp)
        TemplateDate()
        //Title(title = "Dummy Title")
        TemplateTrnsType()
        Description("Treat Transfers As Income Expense\nMinAmount 100 - MaxAmount 500")
    }
}

@Composable
fun ReportTemplateCard(
    title: String,
    accountsSize: Int,
    categorySize: Int,
    compulsoryContent: Map<TemplateLabels, String>,
    optionalContent: Map<TemplateLabels, String> = emptyMap(),
) {
    Column(
        modifier = Modifier
            .padding(all = 20.dp)
            .fillMaxWidth()
            .clip(UI.shapes.r4)
            .background(UI.colors.medium, UI.shapes.r4)
            .clickable(onClick = {
                //Todo
            })
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 20.dp)
    ) {
        Title(title = title)

        SpacerVer(height = 8.dp)

        AccountsAndCategory(accountsSize, categorySize)

        SpacerVer(height = 8.dp)

        compulsoryContent.forEach { (filterSections, s) ->
            KeyValuePair(
                label = filterSections.title,
                labelColor = Orange,
                value = s,
                valueColor = UI.colors.pureInverse
            )
        }

        SpacerVer(height = 16.dp)

        optionalContent.forEach { (filterSections, s) ->
            KeyValuePair(
                label = filterSections.title,
                labelColor = Gray,
                value = s,
                valueColor = Gray
            )
        }
    }
}


@Composable
private fun AccountsAndCategory(
    accountsSize: Int,
    categorySize: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        BadgeComponent(
            icon = dummyIconSized(R.drawable.ic_accounts),
            text = "$accountsSize Accounts",
            background = OrangeLight,
            onClick = { },
        )
        SpacerHor(width = 8.dp)
        BadgeComponent(
            icon = dummyIconSized(R.drawable.ic_custom_category_s),
            text = "$categorySize Categories",
            background = RedLight,
            onClick = { },
        )
    }
}

@Composable
private fun Title(
    title: String?,
) {
    if (title != null) {
        SpacerVer(height = 8.dp)
        Text(
            text = title.ifBlank { "[Expecting Name]" },
            modifier = Modifier.padding(horizontal = 4.dp),
            style = UI.typo.b1.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )
    }
}

@Composable
private fun TemplateDate() {
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Orange,
                    fontStyle = UI.typo.nC.fontStyle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = UI.typo.nC.fontSize
                )
            ) {
                append("Period: ")
            }

            withStyle(
                style = SpanStyle(
                    color = UI.colors.pureInverse,
                    fontStyle = UI.typo.nC.fontStyle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = UI.typo.nC.fontSize
                )
            ) {
                append("From Sep 01 - Sep 31")
            }
            //append("orld")
        },
        modifier = Modifier.padding(start = 4.dp),
    )


//    Text(
//        modifier = Modifier.padding(bottom = 1.dp, start = 4.dp),
//        text = "Period: From Sep 01 - Sep 31",
//        style = UI.typo.nC.style(
//            color = Orange,
//            fontWeight = FontWeight.ExtraBold
//        )
//    )
}

@Composable
private fun TemplateTrnsType() {
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Orange,
                    fontStyle = UI.typo.nC.fontStyle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = UI.typo.nC.fontSize
                )
            ) {
                append("Trn Type: ")
            }

            withStyle(
                style = SpanStyle(
                    color = UI.colors.pureInverse,
                    fontStyle = UI.typo.nC.fontStyle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = UI.typo.nC.fontSize
                )
            ) {
                append("Income, Expense")
            }
            //append("orld")
        },
        modifier = Modifier.padding(start = 4.dp),
    )
}

@Composable
private fun Description(
    description: String?,
) {
    if (description != null) {
        SpacerVer(8.dp)
        Text(
            text = description,
            modifier = Modifier.padding(horizontal = 4.dp),
            style = UI.typo.nC.style(
                color = UI.colors.gray,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun KeyValuePair(label: String, labelColor: Color, value: String, valueColor: Color) {
    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = labelColor,
                    fontStyle = UI.typo.nC.fontStyle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = UI.typo.nC.fontSize
                )
            ) {
                append("$label: ")
            }

            withStyle(
                style = SpanStyle(
                    color = valueColor,
                    fontStyle = UI.typo.nC.fontStyle,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = UI.typo.nC.fontSize
                )
            ) {
                append(value)
            }
        },
        modifier = Modifier.padding(start = 4.dp),
    )
}

