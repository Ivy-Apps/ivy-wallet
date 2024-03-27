plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.ivy.wallet"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "com.ivy.wallet"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.compile.sdk.get().toInt()
        versionName = libs.versions.version.name.get()
        versionCode = libs.versions.version.code.get().toInt()
    }

    androidResources {
        generateLocaleConfig = true
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../debug.jks")
            storePassword = "IVY7834!DEbug"
            keyAlias = "debug"
            keyPassword = "IVY7834!DEbug"
        }

        create("release") {
            storeFile = file("../sign.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            isDebuggable = false
            isDefault = false

            signingConfig = signingConfigs.getByName("release")

            resValue("string", "app_name", "Ivy Wallet")
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false

            isDebuggable = true
            isDefault = true

            signingConfig = signingConfigs.getByName("debug")

            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Ivy Wallet Debug")
        }

        create("demo") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            matchingFallbacks.add("release")

            isDebuggable = false
            isDefault = false

            signingConfig = signingConfigs.getByName("debug")

            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Ivy Wallet Demo")
        }
    }

    val javaVersion = libs.versions.jvm.target.get()
    kotlinOptions {
        jvmTarget = javaVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
        targetCompatibility = JavaVersion.valueOf("VERSION_$javaVersion")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    lint {
        disable += "ComposeViewModelInjection"
        checkDependencies = true
        abortOnError = false
        checkReleaseBuilds = false
        htmlReport = true
        htmlOutput = file("${project.rootDir}/build/reports/lint/lint.html")
        xmlReport = true
        xmlOutput = file("${project.rootDir}/build/reports/lint/lint.xml")
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.data)
    implementation(projects.shared.domain)
    implementation(projects.shared.navigation)
    implementation(projects.screen.attributions)
    implementation(projects.screen.balance)
    implementation(projects.screen.budgets)
    implementation(projects.screen.categories)
    implementation(projects.screen.contributors)
    implementation(projects.screen.exchangeRates)
    implementation(projects.screen.features)
    implementation(projects.screen.home)
    implementation(projects.screen.importData)
    implementation(projects.screen.loans)
    implementation(projects.screen.main)
    implementation(projects.screen.onboarding)
    implementation(projects.screen.piechart)
    implementation(projects.screen.plannedPayments)
    implementation(projects.screen.releases)
    implementation(projects.screen.reports)
    implementation(projects.screen.search)
    implementation(projects.screen.settings)
    implementation(projects.screen.editTransaction)
    implementation(projects.screen.transactions)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)
    implementation(projects.widget.addTransaction)
    implementation(projects.widget.balance)

    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.kotlin.android)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.arrow)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.activity)
    implementation(libs.bundles.google)
    implementation(libs.bundles.firebase)
    implementation(libs.datastore)
    implementation(libs.androidx.security)
    implementation(libs.androidx.biometrics)

    implementation(libs.bundles.hilt)
    implementation(libs.material)
    ksp(libs.hilt.compiler)

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    implementation(libs.timber)
    implementation(libs.keval)
    implementation(libs.bundles.opencsv)
    implementation(libs.androidx.work)
    implementation(libs.androidx.recyclerview)

    testImplementation(libs.bundles.testing)
    testImplementation(libs.androidx.work.testing)

    lintChecks(libs.slack.lint.compose)
}

dependencies {
    koverReport {
        defaults {
            mergeWith("debug")
            html {
                onCheck = false
                setReportDir(layout.buildDirectory.dir("artifacts/reports/kover/coverageResults"))
            }
        }
    }

    kover(projects.shared.base)
    kover(projects.shared.data)
    kover(projects.shared.domain)
    kover(projects.shared.navigation)
    kover(projects.shared.resources)
    kover(projects.widget.sharedBase)
    kover(projects.screen.attributions)
    kover(projects.screen.balance)
    kover(projects.screen.budgets)
    kover(projects.screen.categories)
    kover(projects.screen.contributors)
    kover(projects.screen.exchangeRates)
    kover(projects.screen.features)
    kover(projects.screen.home)
    kover(projects.screen.importData)
    kover(projects.screen.loans)
    kover(projects.screen.main)
    kover(projects.screen.onboarding)
    kover(projects.screen.piechart)
    kover(projects.screen.plannedPayments)
    kover(projects.screen.releases)
    kover(projects.screen.reports)
    kover(projects.screen.search)
    kover(projects.screen.settings)
    kover(projects.screen.editTransaction)
    kover(projects.screen.transactions)
    kover(projects.widget.addTransaction)
    kover(projects.widget.balance)
}
