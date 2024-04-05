plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.transaction"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    
    implementation(projects.temp.oldDesign)
    
    implementation(projects.temp.legacyCode)
    implementation(projects.widget.balance)
    implementation(projects.shared.data)

    testImplementation(projects.shared.testing)
}
