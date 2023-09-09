plugins {
    id("ivy.module")
    id("ivy.room")
}

android {
    namespace = "com.ivy.core"
}

dependencies {
    implementation(projects.ivyDesign)

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)
}