plugins {
    id("ivy.module")
}

android {

}

dependencies {
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
}