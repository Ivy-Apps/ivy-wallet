// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Run with:
    // ./gradlew detekt // Simple report in the console
    // ./gradlew detektFormat // To check with enabled auto-correction
    id("ivy.detekt")

    alias(libs.plugins.gradleWrapperUpgrade)

    alias(libs.plugins.koverPlugin)
}

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
    koverReport {
        // filters for all report types of all build variants
        filters {
            excludes {
                classes(
                    "*Activity",
                    "*Activity\$*",
                    "*.BuildConfig",
                    "dagger.hilt.*",
                    "hilt_aggregated_deps.*",
                    "*.Hilt_*"
                )
                annotatedBy("@Composable")
            }
        }
    }
}

wrapperUpgrade {
    gradle {
        create("ivyWallet") {
            repo.set("Ivy-Apps/ivy-wallet")
            baseBranch.set("main")
        }
    }
}
