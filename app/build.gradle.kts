plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.wbrawner.nanoflux"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
        kotlinCompilerVersion = rootProject.extra["kotlinVersion"] as String
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.bundles.compose)
    implementation(libs.preference)
    implementation(libs.lifecycle)
    implementation(libs.timber)
    implementation(libs.room.ktx)
    kapt(libs.room.kapt)
    implementation(libs.bundles.networking)
    implementation(libs.bundles.moshi)
    kapt(libs.moshi.kapt)
    implementation(libs.hilt.android.core)
    kapt(libs.hilt.android.kapt)
    implementation(libs.hilt.work.core)
    kapt(libs.hilt.work.kapt)
    implementation(libs.work.core)
    testImplementation(libs.work.test)
    testImplementation(libs.junit)
    testImplementation(libs.room.test)
    androidTestImplementation(libs.test.ext)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.compose.test)
}