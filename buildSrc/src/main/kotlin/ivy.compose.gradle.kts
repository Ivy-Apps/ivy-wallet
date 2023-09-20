plugins {
    id("ivy.module")
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
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    implementation(libs.bundles.compose)

    lintChecks(libs.slack.lint.compose)
}