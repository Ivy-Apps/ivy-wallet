plugins {
    id("ivy.module")
    id("ivy.room")
}

android {
    namespace = "com.ivy.core"
}

dependencies {
    implementation(projects.ivyDesign)
    implementation(projects.ivyResources)

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)
    implementation(libs.gson)
    implementation(libs.bundles.opencsv)
    implementation(libs.bundles.firebase)
}