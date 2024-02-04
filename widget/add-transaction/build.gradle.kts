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
    implementation(projects.shared.resources)
    implementation(projects.widget.sharedBase)
}
