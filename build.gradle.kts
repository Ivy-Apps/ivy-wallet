// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(com.ivy.wallet.buildsrc.Libs.Google.playServicesPlugin)

        classpath(com.ivy.wallet.buildsrc.Libs.androidGradlePlugin)
        classpath(com.ivy.wallet.buildsrc.Libs.Kotlin.gradlePlugin)
        classpath(com.ivy.wallet.buildsrc.Libs.Kotlin.androidExtensions)

        classpath(com.ivy.wallet.buildsrc.Libs.Google.Firebase.crashlyticsPlugin)
        classpath(com.ivy.wallet.buildsrc.Libs.Hilt.hiltPlugin)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}