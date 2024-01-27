plugins {
    id("ivy.module")
    id("androidx.room")
}

dependencies {
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    androidTestImplementation(libs.room.testing)
}

room {
    schemaDirectory("$projectDir/schemas")
}
