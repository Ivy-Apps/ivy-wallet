import com.ivy.wallet.buildsrc.DependencyType
import com.ivy.wallet.buildsrc.Project
import com.ivy.wallet.buildsrc.allDeps

plugins {
    //must have full full qualifier else won't build
    val plugins = com.ivy.wallet.buildsrc.allDeps()
        .filter { it.type == com.ivy.wallet.buildsrc.DependencyType.PLUGIN_ID }

    plugins.forEach { plugin ->
        id(plugin.value)
    }
}


android {
    compileSdk = Project.compileSdkVersion

    defaultConfig {
        applicationId = Project.applicationId
        minSdk = Project.minSdk
        targetSdk = Project.targetSdk
        versionCode = Project.versionCode
        versionName = Project.versionName

        testInstrumentationRunner = "com.ivy.wallet.HiltTestRunner"

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../debug.jks")
            storePassword = "IVY7834!DEbug"
            keyAlias = "debug"
            keyPassword = "IVY7834!DEbug"
        }

        create("release") {
            storeFile = file("../sign.jks")
            storePassword = System.getenv("SIGNING_STORE_PASSWORD")
            keyAlias = System.getenv("SIGNING_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            isDefault = false

            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "app_name", "Ivy Wallet")
        }

        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isDefault = true

            signingConfig = signingConfigs.getByName("debug")

            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Ivy Wallet Debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = com.ivy.wallet.buildsrc.composeVersion
    }

    lint {
        isCheckReleaseBuilds = true
        isAbortOnError = false
    }

    packagingOptions {
        //Exclude this files so Jetpack Compose UI tests can build
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
        //-------------------------------------------------------
    }
}

dependencies {
    appDeps()
}

fun DependencyHandlerScope.appDeps() {
    val deps = allDeps()

    deps.forEach { dep ->
        when (dep.type) {
            DependencyType.CLASSPATH -> {
                //do nothing
            }
            DependencyType.IMPLEMENTATION -> {
                implementation(dep.value)
            }
            DependencyType.KAPT -> {
                kapt(dep.value)
            }
            DependencyType.TEST_IMPLEMENTATION -> {
                testImplementation(dep.value)
            }
            DependencyType.ANDROID_TEST_IMPLEMENTATION -> {
                androidTestImplementation(dep.value)
            }
            DependencyType.KAPT_ANDROID_TEST -> {
                kaptAndroidTest(dep.value)
            }
            DependencyType.PLUGIN_ID -> {
                //do nothing
            }
            DependencyType.PLATFORM_BOM -> {
                implementation(platform(dep.value))
            }
        }
    }
}
