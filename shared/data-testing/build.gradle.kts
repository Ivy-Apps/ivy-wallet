plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.data.testing"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.resources)
    implementation(projects.shared.designSystem)
    implementation(projects.shared.domain)
    implementation(projects.shared.navigation)
    implementation(projects.shared.commonUi)
    implementation(projects.shared.data)

    testImplementation(projects.shared.testing)
}
