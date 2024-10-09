plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) // Ensure that these aliases are correctly set in your version catalog
}

android {
    namespace = "com.example.livingdream"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.livingdream"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.core:core-ktx:1.12.0") // Make sure this line is present


    // MPAndroidChart dependency
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
