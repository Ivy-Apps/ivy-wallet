plugins {
    id("ivy.module")
}

dependencies {
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}