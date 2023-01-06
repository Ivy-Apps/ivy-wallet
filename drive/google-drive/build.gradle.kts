import com.ivy.buildsrc.Google
import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Testing
import com.ivy.buildsrc.Timber

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
}

dependencies {
    implementation(project(":common:main"))

    // region Google Drive deps
    // TODO: Extract to "dependencies.gradle.kts" in buildSrc
    implementation("com.google.apis:google-api-services-drive:v3-rev136-1.25.0") {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    implementation("com.google.http-client:google-http-client-gson:1.26.0")
    implementation("com.google.api-client:google-api-client-android:1.26.0") {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    implementation("com.google.guava:guava:28.1-android")
    // endregion

    Hilt()
    Google()
    Timber(api = true)
    Testing()
}