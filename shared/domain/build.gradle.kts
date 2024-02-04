plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.core"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.data)

    implementation(libs.datastore) // TODO: Will be removed
}
