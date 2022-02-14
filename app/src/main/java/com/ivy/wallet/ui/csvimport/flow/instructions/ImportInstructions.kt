package com.ivy.wallet.ui.csvimport.flow.instructions

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.ivy.design.api.navigation
import com.ivy.wallet.R
import com.ivy.wallet.base.drawColoredShadow
import com.ivy.wallet.logic.csv.model.ImportType
import com.ivy.wallet.ui.IvyActivity
import com.ivy.wallet.ui.IvyAppPreview
import com.ivy.wallet.ui.onboarding.components.OnboardingToolbar
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.GradientCutBottom
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.OnboardingButton

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ImportInstructions(
    hasSkip: Boolean,
    importType: ImportType,

    onSkip: () -> Unit,
    onUploadClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        stickyHeader {
            val nav = navigation()
            OnboardingToolbar(
                hasSkip = hasSkip,
                onBack = { nav.onBackPressed() },
                onSkip = onSkip
            )
            //onboarding toolbar include paddingBottom 16.dp
        }

        item {
            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = "How to import",
                style = Typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = "open",
                style = Typo.body2.style(
                    color = Gray,
                    fontWeight = Bold
                )
            )

            Spacer(Modifier.height(24.dp))

            App(
                importType = importType
            )

            Spacer(Modifier.height(24.dp))

            IvyDividerLine(
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        item {
            Spacer(Modifier.height(24.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = "Steps",
                style = Typo.body1.style(
                    fontWeight = FontWeight.Black
                )
            )

            importType.ImportSteps(
                onUploadClick = onUploadClick
            )
        }

        item {
            //last spacer
            Spacer(Modifier.height(96.dp))
        }
    }

    GradientCutBottom(
        height = 96.dp
    )
}

@Composable
fun VideoArticleRow(
    videoUrl: String?,
    articleUrl: String?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val ivyActivity = LocalContext.current as IvyActivity

        Spacer(Modifier.width(16.dp))

        if (videoUrl != null) {
            VideoButton(
                modifier = Modifier.weight(1f)
            ) {
                ivyActivity.openUrlInBrowser(videoUrl)
            }
        }

        if (videoUrl != null && articleUrl != null) {
            Spacer(Modifier.width(8.dp))
        }

        if (articleUrl != null) {
            ArticleButton(
                modifier = Modifier.weight(1f)
            ) {
                ivyActivity.openUrlInBrowser(articleUrl)
            }
        }

        Spacer(Modifier.width(16.dp))
    }
}

@Composable
fun VideoButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    InstructionButton(
        modifier = modifier,
        icon = R.drawable.ic_import_video,
        caption = "How to",
        text = "Video"
    ) {
        onClick()
    }
}

@Composable
fun ArticleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    InstructionButton(
        modifier = modifier,
        icon = R.drawable.ic_import_web,
        caption = "How to",
        text = "Article"
    ) {
        onClick()
    }
}

@Composable
fun InstructionButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int?,
    caption: String,
    text: String,

    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(Shapes.rounded20)
            .background(IvyTheme.colors.medium, Shapes.rounded20)
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        if (icon != null) {
            IvyIcon(
                modifier = Modifier.background(IvyTheme.colors.pure, CircleShape),
                icon = icon,
                tint = Color.Unspecified
            )
        }

        Spacer(Modifier.width(if (icon != null) 24.dp else 12.dp))

        Column {
            Text(
                text = caption,
                style = Typo.caption.style(
                    color = Gray,
                    fontWeight = Bold
                )
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = text,
                style = Typo.body2.style(
                    fontWeight = Bold
                )
            )
        }
    }
}

@Composable
fun UploadFileStep(
    stepNumber: Int,
    onUploadClick: () -> Unit
) {
    StepTitle(
        number = stepNumber,
        title = "Upload CSV file"
    )

    Spacer(Modifier.height(16.dp))

    OnboardingButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = "Upload CSV file",
        textColor = White,
        backgroundGradient = GradientIvy,
        hasNext = false,
        iconStart = R.drawable.ic_upload
    ) {
        onUploadClick()
    }
}

@Composable
fun StepTitle(
    number: Int,
    title: String,
    description: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(24.dp))

        Text(
            modifier = Modifier
                .size(24.dp)
                .background(IvyTheme.colors.medium, CircleShape),
            text = number.toString(),
            style = Typo.numberBody2.style(
                fontWeight = Bold,
                textAlign = TextAlign.Center
            )
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 32.dp),
            text = title,
            style = Typo.body2.style(
                fontWeight = Bold
            )
        )
    }

    if (description != null) {
        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = description,
            style = Typo.caption.style(
                fontWeight = Bold,
                color = Gray
            )
        )
    }

}

@Composable
private fun App(
    importType: ImportType
) {
    val ivyActivity = LocalContext.current as IvyActivity

    Row(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .clickable {
                ivyActivity.openGooglePlayAppPage(
                    appId = importType.appId()
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        IvyIcon(
            modifier = Modifier
                .drawColoredShadow(importType.color())
                .size(48.dp),
            icon = importType.logo(),
            tint = Color.Unspecified
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = importType.appName(),
            style = Typo.body2.style(
                fontWeight = Bold
            )
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    IvyAppPreview {
        ImportInstructions(
            hasSkip = true,
            importType = ImportType.MONEY_MANAGER,
            onSkip = {}
        ) {

        }
    }
}

