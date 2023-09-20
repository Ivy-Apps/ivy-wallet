plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.testing"
}

dependencies {
    implementation(libs.bundles.testing)
}