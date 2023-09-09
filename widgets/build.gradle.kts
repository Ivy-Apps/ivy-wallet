plugins {
    id("ivy.module")
}

android {
    namespace = "com.ivy.widgets"
}

dependencies {
    implementation(projects.ivyResources)
}