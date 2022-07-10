package com.ivy.import_data.flow.instructions

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.IvyWalletPreview
import com.ivy.base.R
import com.ivy.base.rootScreen
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.navigation
import com.ivy.import_data.flow.ImportSteps
import com.ivy.old.OnboardingToolbar
import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportType
import com.ivy.wallet.ui.theme.GradientIvy
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.GradientCutBottom
import com.ivy.wallet.ui.theme.components.IvyDividerLine
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.OnboardingButton
import com.ivy.wallet.utils.drawColoredShadow

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
                text = stringResource(R.string.how_to_import),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = stringResource(R.string.open),
                style = UI.typo.b2.style(
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
                text = stringResource(R.string.steps),
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Black
                )
            )

            ImportSteps(
                type = importType,
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
        val rootScreen = rootScreen()

        Spacer(Modifier.width(16.dp))

        if (videoUrl != null) {
            VideoButton(
                modifier = Modifier.weight(1f)
            ) {
                rootScreen.openUrlInBrowser(videoUrl)
            }
        }

        if (videoUrl != null && articleUrl != null) {
            Spacer(Modifier.width(8.dp))
        }

        if (articleUrl != null) {
            ArticleButton(
                modifier = Modifier.weight(1f)
            ) {
                rootScreen.openUrlInBrowser(articleUrl)
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
        caption = stringResource(R.string.how_to),
        text = stringResource(R.string.video)
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
        caption = stringResource(R.string.how_to),
        text = stringResource(R.string.article)
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
            .clip(UI.shapes.r3)
            .background(UI.colors.medium, UI.shapes.r3)
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(16.dp))

        if (icon != null) {
            IvyIcon(
                modifier = Modifier.background(UI.colors.pure, CircleShape),
                icon = icon,
                tint = Color.Unspecified
            )
        }

        Spacer(Modifier.width(if (icon != null) 24.dp else 12.dp))

        Column {
            Text(
                text = caption,
                style = UI.typo.c.style(
                    color = Gray,
                    fontWeight = Bold
                )
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = text,
                style = UI.typo.b2.style(
                    fontWeight = Bold
                )
            )
        }
    }
}

@Composable
fun UploadFileStep(
    stepNumber: Int,
    text: String = stringResource(R.string.upload_csv_file),
    onUploadClick: () -> Unit
) {
    StepTitle(
        number = stepNumber,
        title = text
    )

    Spacer(Modifier.height(16.dp))

    OnboardingButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        text = text,
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
                .background(UI.colors.medium, CircleShape),
            text = number.toString(),
            style = UI.typo.nB2.style(
                fontWeight = Bold,
                textAlign = TextAlign.Center
            )
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 32.dp),
            text = title,
            style = UI.typo.b2.style(
                fontWeight = Bold
            )
        )
    }

    if (description != null) {
        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = description,
            style = UI.typo.c.style(
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
    val rootScreen = rootScreen()

    Row(
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .clickable {
                rootScreen.openGooglePlayAppPage(
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
            style = UI.typo.b2.style(
                fontWeight = Bold
            )
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        ImportInstructions(
            hasSkip = true,
            importType = ImportType.MONEY_MANAGER,
            onSkip = {}
        ) {

        }
    }
}

