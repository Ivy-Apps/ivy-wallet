plugins {
    id("ivy.feature")
    id("ivy.room")
}

android {
    namespace = "com.ivy.legacy"
}

dependencies {
    implementation(projects.shared.base)
    implementation(projects.shared.domain)
    
    implementation(projects.temp.oldDesign)
    
    implementation(projects.shared.data)

    implementation(libs.bundles.activity)
    implementation(libs.bundles.opencsv)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.ktor)
    implementation(libs.androidx.work)
    implementation(libs.datastore)
    implementation(libs.keval)
    implementation(libs.androidx.recyclerview)
}
