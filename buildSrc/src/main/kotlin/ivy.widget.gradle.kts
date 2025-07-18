plugins {
    id("ivy.module")
    id("ivy.compose")
}

dependencies {
    implementation(libs.bundles.glance)
    implementation(libs.bundles.activity)
}
