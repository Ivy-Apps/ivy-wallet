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
    implementation(projects.shared.ui.core)
    implementation(projects.shared.ui.navigation)

    implementation(projects.widget.sharedBase)
}
