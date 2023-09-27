plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.core"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyPersistence)

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)
}