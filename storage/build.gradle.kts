plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.4.32"
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = libs.versions.maxSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.maxSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        javaCompileOptions {
            annotationProcessorOptions {
                arguments.putAll(mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                ))
            }
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
}

dependencies {
    implementation(project(":common"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core)
    implementation(libs.preference)
    implementation(libs.room.ktx)
    kapt(libs.room.kapt)
    api(libs.androidx.paging)
    implementation(libs.bundles.coroutines)
    implementation(libs.kotlinx.serialization)
    implementation(libs.hilt.android.core)
    kapt(libs.hilt.android.kapt)
    testImplementation(libs.junit)
    testImplementation(libs.room.test)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.espresso)
}