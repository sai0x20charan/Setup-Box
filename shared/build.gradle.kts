plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.charan.shared"
    compileSdk {
        version = release(36)
    }
    val key:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_ANON_KEY")

    val url:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_URL")

    val google_signin = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("GOOGLE_SERVER_CLIENT_ID")

    val key_debug:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_ANON_KEY_DEBUG")

    val url_debug:String=com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir,providers).getProperty("SUPABASE_URL_DEBUG")

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.postgrest.kt)
    implementation(libs.auth.kt)
    implementation(libs.realtime.kt)
    implementation(libs.functions.kt)

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

    // Firebase
    implementation(libs.firebase.crashlytics)

    //Google Sign-In
    implementation(libs.androidx.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)
}
