plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.transaction"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    implementation(projects.shared.resources)
    implementation(projects.temp.oldDesign)
    implementation(projects.shared.navigation)
    implementation(projects.temp.legacyCode)
    implementation(projects.widget.balance)
    implementation(projects.shared.data)

    testImplementation(projects.shared.testing)
}
