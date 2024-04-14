plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.design"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.ui.core)

    implementation(projects.shared.domain)
}