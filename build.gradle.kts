import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("android-reporting")
    // Run with:
    // ./gradlew dependencyUpdates // Simple report in the console
    // ./gradlew dependencyUpdates -DoutputFormatter=html,json,xml // Report in console & generate files accordingly
    id("com.github.ben-manes.versions") version "0.39.0"

    // Kotest Plugin
    // https://github.com/kotest/kotest-gradle-plugin
    id("io.kotest") version "0.3.8"
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }

    withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isNonStable(candidate.version)
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Any of parameter of this task can be passed on or changed when running the gradle task as parameter
tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    outputFormatter = "html"
    outputDir = "build/reports/dependencyUpdates"
    reportfileName = "report"
}

// https://github.com/ben-manes/gradle-versions-plugin#rejectversionsif-and-componentselection
// This has been tested thoroughly by community
fun isNonStable(version: String): Boolean {
    val stableKeyword =
        listOf("RELEASE", "FINAL", "GA", "RC").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}