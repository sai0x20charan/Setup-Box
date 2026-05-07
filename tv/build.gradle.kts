plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.ksp)

}

android {
    namespace = "com.charan.setupBox"
    compileSdk = 36
    val key:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_ANON_KEY")

    val url:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_URL")

    defaultConfig {
        applicationId = "com.charan.setupBox"
        minSdk = 23
        targetSdk = 36
        versionCode = 3
        versionName = "2.1"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String","SUPABASE_ANON_KEY","\"$key\"")
        buildConfigField("String", "SUPABASE_URL", "\"$url\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    hilt { enableAggregatingTask = false }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":shared"))

    // AndroidX / Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)

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

    // Compose utilities
    implementation(libs.compose.material)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.serialization.json)

    // Firebase
    implementation(libs.firebase.crashlytics)

    // Test / debug
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
