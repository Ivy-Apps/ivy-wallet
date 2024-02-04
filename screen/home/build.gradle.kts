plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.home"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    implementation(projects.shared.resources)
    implementation(projects.temp.oldDesign)
    implementation(projects.shared.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.shared.data)
    implementation(projects.shared.commonUi)

    implementation(projects.widget.addTransaction)

    testImplementation(projects.shared.testing)
}
