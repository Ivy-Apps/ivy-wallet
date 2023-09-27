plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.contributors"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyDomain)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyNavigation)
    implementation(projects.ivyDomainUi)

    implementation(libs.bundles.ktor)

    implementation(projects.ivyDesign)
}