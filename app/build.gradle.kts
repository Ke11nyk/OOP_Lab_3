plugins {
    id (Plugin.androidApplication)
    id (Plugin.androidKotlin)
    kotlin(Plugin.kotlinAndroidKapt)
    id (Plugin.daggerHilt)
}

android {
    namespace = Apps.applicationId
    compileSdk = Apps.compileSdk

    defaultConfig {
        applicationId = Apps.applicationId
        minSdk = Apps.minSdk
        targetSdk = Apps.targetSdk
        versionCode = Apps.versionCode
        versionName = Apps.versionName

        testInstrumentationRunner = TestDependencies.instrumentationRunner
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    android.apply {
        dataBinding.enable = true
        viewBinding.enable = true
    }
}

dependencies {
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation(project(":design"))
    implementation(project(":scanner"))
    implementation("com.google.firebase:firebase-encoders-json:17.1.0")
    androidX()
    daggerHilt()
    testEspressoCore()
    jUnit()
    navigation()
}