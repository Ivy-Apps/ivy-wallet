plugins {
    id("ivy.widget")
}

android {
    namespace = "com.ivy.widget"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
}