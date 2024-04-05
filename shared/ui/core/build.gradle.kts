plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.ui"
}

dependencies {
    implementation(projects.shared.domain)

    testImplementation(projects.shared.testing)
}
