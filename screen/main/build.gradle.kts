plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.main"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    
    
    
    implementation(projects.shared.ui.core)
    implementation(projects.shared.data)

    implementation(projects.screen.home)
    implementation(projects.screen.accounts)

    implementation(projects.temp.oldDesign)
    implementation(projects.temp.legacyCode)

    testImplementation(projects.shared.testing)
}
