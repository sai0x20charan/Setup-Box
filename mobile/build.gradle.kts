plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mikepenz.aboutlibrary)
    alias(libs.plugins.google.ksp)

}

android {
    namespace = "com.charan.setupBox"
    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }
    val key:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_ANON_KEY")

    val url:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_URL")

    val google_signin = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("GOOGLE_SERVER_CLIENT_ID")

    val key_debug:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_ANON_KEY_DEBUG")

    val url_debug:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_URL_DEBUG")



    defaultConfig {
        applicationId = "com.charan.setupBox"
        minSdk = 26
        targetSdk = 37
        versionCode = 3
        versionName = "2.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"$key\"")
            buildConfigField("String", "SUPABASE_URL", "\"$url\"")
            buildConfigField("String", "GOOGLE_SERVER_CLIENT_ID", "\"$google_signin\"")
        }
        debug {
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"$key_debug\"")
            buildConfigField("String", "SUPABASE_URL", "\"$url_debug\"")
            buildConfigField("String", "GOOGLE_SERVER_CLIENT_ID", "\"$google_signin\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion ="1.5.2"
    }
    hilt { enableAggregatingTask = false }
}

dependencies {
    // AndroidX / UI
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.material3.android)
    implementation(libs.material)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.compose.material.icons.extended.android)

    // Navigation / lifecycle
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.runtime.livedata)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.postgrest.kt)
    implementation(libs.auth.kt)
    implementation(libs.realtime.kt)

    // Networking (Ktor)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    // DI
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Google sign-in / identity
    implementation(libs.androidx.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)

    // Compose utilities
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.serialization.json)

    // About screen
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.m3)

    // Firebase
    implementation(libs.firebase.crashlytics)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
