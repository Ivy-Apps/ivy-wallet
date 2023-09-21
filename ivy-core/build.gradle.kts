plugins {
    id("ivy.feature")
    id("ivy.room")
}

android {
    namespace = "com.ivy.core"
}

dependencies {
    implementation(projects.ivyResources) // TODO: Get rid of
    implementation(projects.tempOldDesign) // TODO: Get rid of

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)
}