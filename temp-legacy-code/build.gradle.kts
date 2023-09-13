plugins {
    id("ivy.feature")
    id("ivy.room")
}

android {
    namespace = "com.ivy.legacy"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)

    implementation(libs.bundles.activity)
    implementation(libs.bundles.opencsv)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.ktor)
    implementation(libs.androidx.work)
    implementation(libs.datastore)
    implementation(libs.keval)
    implementation(libs.androidx.recyclerview)
    implementation(libs.gson)
}