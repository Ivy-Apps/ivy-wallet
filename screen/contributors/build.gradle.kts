plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.contributors"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    
    
    
    implementation(projects.shared.ui.core)

    implementation(libs.bundles.ktor)

    testImplementation(projects.shared.testing)
}
