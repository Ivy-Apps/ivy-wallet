plugins {
    id("ivy.module")
    id("app.cash.molecule")
}

android {
    // Compose
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = catalog.version("compose-compiler")
    }

    lint {
        disable += "MissingTranslation"
        disable += "ComposeViewModelInjection"
        abortOnError = false
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

@Suppress("MaximumLineLength", "MaxLineLength")
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        if (project.findProperty("composeCompilerReports") == "true") {
            freeCompilerArgs += listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_compiler"
            )
        }
        if (project.findProperty("composeCompilerMetrics") == "true") {
            freeCompilerArgs += listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_compiler"
            )
        }
    }
}

dependencies {
    implementation(libs.bundles.compose)

    lintChecks(libs.slack.lint.compose)
}