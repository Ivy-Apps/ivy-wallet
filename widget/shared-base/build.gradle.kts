plugins {
    id("ivy.widget")
}

android {
    namespace = "com.ivy.widget"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
}
