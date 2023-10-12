plugins {
    id("ivy.feature")
    kotlin("kapt")
}

android {
    namespace = "com.ivy.googledrive"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyResources)
    implementation(projects.tempLegacyCode)
    api(libs.google.services.drive) {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    implementation(libs.google.http.gson)
    implementation(libs.google.http.android) {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    implementation(libs.google.guava)
    api(libs.google.playservices.auth)

    implementation(libs.bundles.hilt)
    api(libs.androidx.work)
}
