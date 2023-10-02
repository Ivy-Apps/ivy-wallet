plugins {
    id("ivy.feature")
    id("ivy.room")
}

android {
    namespace = "com.ivy.persistence"
}

dependencies {
    implementation(projects.ivyBase)

    implementation(libs.datastore)
    implementation(libs.bundles.ktor)
}