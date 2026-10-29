plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
//    KSP AND HILT
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
//    serialization
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.example.newsapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.newsapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
//  Retrofitfor networking
    implementation(libs.retrofit)
//  Room databse dependency
    implementation(libs.androidx.room.runtime)
//  Optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx.v285)
    ksp(libs.androidx.room.compiler)
//  Import this in order to use view models
    implementation(platform(libs.androidx.compose.bom))
//  Import material icon library
    implementation(libs.androidx.compose.material.icons.core)
//  Navigation library
    implementation(libs.androidx.navigation.compose)
//  Hilt and hilt compiler
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
//-------------------------------------
//  Hilt navigations compose for viewmodel injection
    implementation(libs.androidx.hilt.navigation.compose)
//    JSON serialization depedency
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}