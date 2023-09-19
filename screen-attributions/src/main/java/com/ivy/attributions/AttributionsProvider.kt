package com.ivy.attributions

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import javax.inject.Inject

class AttributionsProvider @Inject constructor() {
    private val attributionItems = persistentListOf(
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
        AttributionItem.Attribution(
            name = "Kotlin",
            link = "https://kotlinlang.org"
        ),
        AttributionItem.Attribution(
            name = "Jetpack Compose",
            link = "https://developer.android.com/jetpack/compose"
        ),
        AttributionItem.Attribution(
            name = "Kotlin Coroutines",
            link = "https://kotlinlang.org/docs/coroutines-overview.html"
        ),
        AttributionItem.Attribution(
            name = "Kotlin Flow",
            link = "https://kotlinlang.org/docs/flow.html"
        ),
        AttributionItem.Attribution(
            name = "Hilt",
            link = "https://dagger.dev/hilt"
        ),
        AttributionItem.Attribution(
            name = "ArrowKt",
            link = "https://arrow-kt.io"
        ),
        AttributionItem.Attribution(
            name = "Kotest",
            link = "https://kotest.io"
        ),
        AttributionItem.Attribution(
            name = "DataStore",
            link = "https://developer.android.com/topic/libraries/architecture/datastore"
        ),
        AttributionItem.Attribution(
            name = "Room DB",
            link = "https://developer.android.com/training/data-storage/room"
        ),
        AttributionItem.Attribution(
            name = "Ktor Client",
            link = "https://ktor.io/docs/getting-started-ktor-client.html"
        ),
        AttributionItem.Attribution(
            name = "KotlinX Serialization",
            link = "https://github.com/Kotlin/kotlinx.serialization"
        ),
        AttributionItem.Attribution(
            name = "Gradle KTS (Kotlin DSL)",
            link = "https://docs.gradle.org/current/userguide/kotlin_dsl.html"
        ),
        AttributionItem.Attribution(
            name = "Fastlane",
            link = "https://fastlane.tools"
        ),
        AttributionItem.Attribution(
            name = "Github Actions",
            link = "https://github.com/Ivy-Apps/ivy-wallet/actions"
        ),
        AttributionItem.Attribution(
            name = "Timber",
            link = "https://github.com/JakeWharton/timber"
        ),
        AttributionItem.Attribution(
            name = "Detekt",
            link = "https://github.com/detekt/detekt"
        ),
        AttributionItem.Attribution(
            name = "Ktlint",
            link = "https://github.com/pinterest/ktlint"
        ),
        AttributionItem.Attribution(
            name = "Slack's compose-lints",
            link = "https://slackhq.github.io/compose-lints"
        )
    )

    fun provideAttributions(): ImmutableList<AttributionItem> {
        return attributionItems
    }
}