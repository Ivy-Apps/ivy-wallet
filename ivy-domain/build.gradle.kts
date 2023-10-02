plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.core"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyData)

    implementation(libs.datastore) // TODO: Will be removed
}