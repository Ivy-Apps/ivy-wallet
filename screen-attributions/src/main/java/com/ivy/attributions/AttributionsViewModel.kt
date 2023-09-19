package com.ivy.attributions

import androidx.compose.runtime.Composable
import com.ivy.core.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttributionsViewModel @Inject constructor() :
    ComposeViewModel<AttributionsState, AttributionsEvent>() {
    private val attributionItems = listOf<AttributionItem>(
        AttributionItem.Divider(sectionName = "Icons"),
        AttributionItem.Attribution(name = "iconsax", link = "https://iconsax.io"),
        AttributionItem.Attribution(
            name = "Material Symbols Google",
            link = "https://fonts.google.com/icons"
        ),
        AttributionItem.Attribution(
            name = "coolicons",
            link = "https://github.com/krystonschwarze/coolicons"
        ),

        AttributionItem.Divider(sectionName = "Fonts"),
        AttributionItem.Attribution(
            name = "Open Sans",
            link = "https://fonts.google.com/specimen/Open+Sans"
        ),
        AttributionItem.Attribution(
            name = "Raleway",
            link = "https://fonts.google.com/specimen/Raleway?query=raleway"
        ),
        AttributionItem.Attribution(
            name = "Nunito Sans",
            link = "https://fonts.google.com/specimen/Nunito+Sans?query=nunito"
        ),

        AttributionItem.Divider(sectionName = "Tech Stack"),
        AttributionItem.Divider(sectionName = "Core"),
        AttributionItem.Attribution(
            name = "100% Kotlin",
            link = "https://kotlinlang.org"
        ),
        AttributionItem.Attribution(
            name = "100% Jetpack Compose",
            link = "https://developer.android.com/jetpack/compose"
        ),
        AttributionItem.Attribution(
            name = "Kotlin Coroutines (structured concurrency)",
            link = "https://kotlinlang.org/docs/coroutines-overview.html"
        ),
        AttributionItem.Attribution(
            name = "Kotlin Flow (reactivity)",
            link = "https://kotlinlang.org/docs/flow.html"
        ),
        AttributionItem.Attribution(
            name = "Hilt (DI)",
            link = "https://dagger.dev/hilt"
        ),
        AttributionItem.Attribution(
            name = "ArrowKt (functional programming)",
            link = "https://arrow-kt.io"
        ),
        AttributionItem.Attribution(
            name = "Kotest (unit testing)",
            link = "https://kotest.io"
        ),

        AttributionItem.Divider(sectionName = "Local Persistence"),
        AttributionItem.Attribution(
            name = "DataStore (key-value storage)",
            link = "https://developer.android.com/topic/libraries/architecture/datastore"
        ),
        AttributionItem.Attribution(
            name = "Room DB (SQLite ORM)",
            link = "https://developer.android.com/training/data-storage/room"
        ),

        AttributionItem.Divider(sectionName = "Networking"),
        AttributionItem.Attribution(
            name = "Ktor Client (REST client)",
            link = "https://ktor.io/docs/getting-started-ktor-client.html"
        ),
        AttributionItem.Attribution(
            name = "KotlinX Serialization (JSON serialization)",
            link = "https://github.com/Kotlin/kotlinx.serialization"
        ),

        AttributionItem.Divider(sectionName = "Build & CI"),
        AttributionItem.Attribution(
            name = "Gradle KTS (Kotlin DSL)",
            link = "https://docs.gradle.org/current/userguide/kotlin_dsl.html"
        ),
        AttributionItem.Attribution(
            name = "Fastlane (uploads the app to the Google PlayStore)",
            link = "https://fastlane.tools"
        ),
        AttributionItem.Attribution(
            name = "Github Actions (CI/CD)",
            link = "https://github.com/Ivy-Apps/ivy-wallet/actions"
        ),

        AttributionItem.Divider(sectionName = "Other"),
        AttributionItem.Attribution(
            name = "Timber (logging)",
            link = "https://github.com/JakeWharton/timber"
        ),
        AttributionItem.Attribution(
            name = "Detekt (linter)",
            link = "https://github.com/detekt/detekt"
        ),
        AttributionItem.Attribution(
            name = "Ktlint (linter)",
            link = "https://github.com/pinterest/ktlint"
        ),
        AttributionItem.Attribution(
            name = "Slack's compose-lints (linter)",
            link = "https://slackhq.github.io/compose-lints"
        )
    )

    @Composable
    override fun uiState(): AttributionsState {
        return AttributionsState(attributionItems)
    }

    override fun onEvent(event: AttributionsEvent) {}
}