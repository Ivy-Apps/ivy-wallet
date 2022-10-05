import com.ivy.buildsrc.DataStore
import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.RoomDB

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
    `kotlin-kapt` // for Room DB
}

android {
    defaultConfig {
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/../room-db-schemas")
            }
        }
    }
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    implementation(project(":core:domain"))
    implementation(project(":core:persistence"))
    RoomDB(api = false)
    DataStore(api = false)
}