plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.piechart"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    
    implementation(projects.temp.oldDesign)
    
    implementation(projects.temp.legacyCode)
    implementation(projects.shared.data)

    testImplementation(projects.shared.testing)
}
