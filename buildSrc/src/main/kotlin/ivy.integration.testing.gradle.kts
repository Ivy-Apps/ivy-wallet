plugins {
    id("ivy.feature")
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources.pickFirsts.add("win32-x86-64/attach_hotspot_windows.dll")
        resources.pickFirsts.add("win32-x86/attach_hotspot_windows.dll")
        resources.pickFirsts.add("META-INF/**")
    }
}

dependencies {
    androidTestImplementation(libs.bundles.integration.testing)
    androidTestImplementation(project(":ivy-testing"))
}
