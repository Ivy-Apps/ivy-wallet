plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.__module"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.resources)
    implementation(projects.shared.designSystem)
    implementation(projects.shared.domain)
    implementation(projects.shared.navigation)
    implementation(projects.shared.commonUi)

    testImplementation(projects.shared.testing)
}
