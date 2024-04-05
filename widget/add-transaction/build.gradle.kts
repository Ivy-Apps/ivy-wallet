plugins {
    id("ivy.widget")
}

android {
    namespace = "com.ivy.widget.transaction"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    
    implementation(projects.widget.sharedBase)
}
