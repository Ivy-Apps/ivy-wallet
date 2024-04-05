plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.core.ui"
}

dependencies {
    
    
    implementation(projects.shared.domain)
    

    testImplementation(projects.shared.testing)
}
