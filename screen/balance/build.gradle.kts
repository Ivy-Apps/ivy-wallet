plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.balance"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    
    implementation(projects.temp.oldDesign)
    
    implementation(projects.temp.legacyCode)

    testImplementation(projects.shared.testing)
}
