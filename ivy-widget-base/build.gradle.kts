plugins {
    id("ivy.widget")
}

android {
    namespace = "com.ivy.widget-base"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
}