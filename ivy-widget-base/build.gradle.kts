plugins {
    id("ivy.widget")
}

android {
    namespace = "com.ivy.widget"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyDomain)
    implementation(projects.ivyResources)
}