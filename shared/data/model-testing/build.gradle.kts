plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.data.model.testing"
}

dependencies {
    implementation(projects.shared.data.model)

    implementation(libs.bundles.testing)
}