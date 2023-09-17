plugins {
    id("ivy.feature")
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
}