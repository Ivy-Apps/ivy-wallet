import com.ivy.buildsrc.DataStore
import com.ivy.buildsrc.Gson
import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.RoomDB

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
    `kotlin-kapt`
}

android {
    defaultConfig {
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":core:data-model"))
    DataStore(api = true)
    RoomDB(api = true)
    Gson(api = false)
}