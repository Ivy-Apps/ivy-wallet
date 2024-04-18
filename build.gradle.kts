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

    plugins.withId("app.cash.paparazzi") {
        // Defer until afterEvaluate so that testImplementation is created by Android plugin.
        afterEvaluate {
            dependencies.constraints {
                add("testImplementation", "com.google.guava:guava") {
                    attributes {
                        attribute(
                            TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
                            objects.named(TargetJvmEnvironment::class.java  , TargetJvmEnvironment.STANDARD_JVM)
                        )
                    }
                    because("LayoutLib and sdk-common depend on Guava's -jre published variant." +
                            "See https://github.com/cashapp/paparazzi/issues/906.")
                }
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
