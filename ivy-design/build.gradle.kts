plugins {
    id("ivy.module")
}

android {
    namespace = "com.ivy.design"
}

dependencies {
    implementation(projects.ivyResources)
}