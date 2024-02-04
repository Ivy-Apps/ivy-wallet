plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.design"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.resources)
    implementation(projects.shared.designSystem)
    implementation(projects.shared.domain)
}
