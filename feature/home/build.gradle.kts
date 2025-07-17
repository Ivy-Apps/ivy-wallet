plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.home"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.data.core)
    implementation(projects.shared.domain)
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.temp.oldDesign)
    implementation(projects.widget.addTransaction)
    implementation(projects.feature.poll.public)

    testImplementation(projects.shared.ui.testing)
}
