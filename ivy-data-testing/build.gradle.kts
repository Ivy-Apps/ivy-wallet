plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.data.testing"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyDomain)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyCommonUi)
    implementation(projects.ivyData)

    testImplementation(projects.ivyTesting)
}