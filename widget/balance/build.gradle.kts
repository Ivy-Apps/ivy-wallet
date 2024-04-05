plugins {
    id("ivy.widget")
}

android {
    namespace = "com.ivy.widget.balance"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    
    implementation(projects.temp.oldDesign)
    implementation(projects.widget.sharedBase)
    implementation(projects.temp.legacyCode)
}
