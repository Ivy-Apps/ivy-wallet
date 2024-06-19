plugins {
    org.jetbrains.kotlin.plugin.compose
    id("ivy.module")
    id("app.cash.molecule")
}

android {
    // Compose
    buildFeatures {
        compose = true
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

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {
    implementation(libs.bundles.compose)

    lintChecks(libs.slack.lint.compose)
}
