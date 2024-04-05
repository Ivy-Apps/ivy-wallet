plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.ui.testing"
}

dependencies {
    implementation(projects.shared.ui.core)

    // for this module we need test deps as "implementation" and not only "testImplementation"
    // because it'll be added as "testImplementation"
    implementation(libs.bundles.testing)
    implementation(libs.paparazzi)
}
