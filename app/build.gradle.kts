plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.aigiri"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.aigiri"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            // Exclude all META-INF and other duplicate files
            excludes += setOf(
                "/META-INF/**",              // All META-INF files
                "mozilla/**",                // All mozilla folder files
                "**/DEPENDENCIES",
                "**/LICENSE*",
                "**/NOTICE*",
                "**/*.txt"
            )
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Google Maps
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation(libs.play.services.maps)

    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.animation.core.lint)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.location)
    implementation("androidx.biometric:biometric:1.1.0")
    implementation(libs.play.services.maps)


    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Realtime Database for live location sharing
    implementation ("com.google.firebase:firebase-database-ktx")
    implementation("io.ktor:ktor-client-core:2.3.8")
    implementation("io.ktor:ktor-client-cio:2.3.8")
    implementation("io.ktor:ktor-client-android:2.3.8")
    implementation("io.ktor:ktor-client-serialization:2.3.8")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.twilio.sdk:twilio:10.2.0")
    implementation (platform("com.google.firebase:firebase-bom:32.8.1") )// Use BoM
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx")

    // Optional: If you're using Kotlin coroutines with Firebase
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation ("androidx.compose.material:material-icons-extended:1.6.0")
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
    implementation("at.favre.lib:bcrypt:0.9.0")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
//    implementation ("io.livekit:livekit-android:2.18.1")
//    implementation ("io.livekit:livekit-android-camerax:2.+")
//    implementation ("io.livekit:livekit-android-compose-components:1.+")
////    // ✅ Correct artifact from Maven Central
////    implementation("com.twilio:audioswitch:1.1.4")
//
//    implementation("com.infobip:google-webrtc:1.0.43577") {
//        exclude(group = "org.webrtc", module = "webrtc")
//    }
    implementation ("com.github.ZEGOCLOUD:zego_uikit_prebuilt_live_streaming_android:+")
    implementation ("com.google.accompanist:accompanist-permissions:0.35.0-alpha")
//    configurations.all {
//        exclude(group = "com.github.webrtc-sdk", module = "android")
//    }
    implementation ("ai.picovoice:porcupine-android:3.0.3")
    implementation ("org.osmdroid:osmdroid-android:6.1.16")



}

