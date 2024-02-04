plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.core.ui"
}

dependencies {
    implementation(projects.shared.resources)
    implementation(projects.shared.designSystem)
    implementation(projects.shared.domain)
    implementation(projects.shared.navigation)

    testImplementation(projects.shared.testing)
}
