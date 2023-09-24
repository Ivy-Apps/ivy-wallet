plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.design"
}

dependencies {
    implementation(projects.ivyResources)
}