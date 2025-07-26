// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Run with:
    // ./gradlew detekt // Simple report in the console
    // ./gradlew detektFormat // To check with enabled auto-correction
    id("ivy.detekt")
    id("com.jraska.module.graph.assertion")

    alias(libs.plugins.gradleWrapperUpgrade)

    alias(libs.plugins.koverPlugin)
}


/*
 * By listing all the plugins used throughout all subprojects in the root project build script, it
 * ensures that the build script classpath remains the same for all projects. This avoids potential
 * problems with mismatching versions of transitive plugin dependencies. A subproject that applies
 * an unlisted plugin will have that plugin and its dependencies _appended_ to the classpath, not
 * replacing pre-existing dependencies.
 */
plugins {
    alias(libs.plugins.compose) apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
    kover {
        reports {
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
}

wrapperUpgrade {
    gradle {
        create("ivyWallet") {
            repo.set("Ivy-Apps/ivy-wallet")
            baseBranch.set("main")
        }
    }
}
