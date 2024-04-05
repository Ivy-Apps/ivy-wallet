plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.home"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    
    implementation(projects.temp.oldDesign)
    
    implementation(projects.temp.legacyCode)
    implementation(projects.shared.data)
    implementation(projects.shared.ui.core)

    implementation(projects.widget.addTransaction)

    testImplementation(projects.shared.testing)
}
