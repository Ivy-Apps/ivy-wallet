plugins {
    id("ivy.feature")
    id("ivy.room")
}

android {
    namespace = "com.ivy.core"
}

dependencies {
    implementation(libs.datastore)
    implementation(libs.bundles.ktor)
}