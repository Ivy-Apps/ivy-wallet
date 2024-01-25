plugins {
    id("ivy.feature")
}

android {
    namespace = "com.ivy.testing"
}

dependencies {
    implementation(projects.ivyDomain)
    implementation(projects.ivyBase)

    // for this module we need test deps as "implementation" and not only "testImplementation"
    implementation(libs.bundles.testing)
}
