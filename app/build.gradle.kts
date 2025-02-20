import org.gradle.declarative.dsl.schema.FqName.Empty.packageName

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.apollographql.apollo3")
}

android {
    namespace = "com.example.enturcase"
    compileSdk = 35

    packaging {
        resources {
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }

    defaultConfig {
        applicationId = "com.example.enturcase"
        minSdk = 26
        targetSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // gson
    implementation("com.google.code.gson:gson:2.10.1")
    // graphql
    implementation("com.apollographql.apollo3:apollo-runtime:3.8.2")
    // navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")
    // permissions
    implementation("com.google.accompanist:accompanist-permissions:0.33.2-alpha")
    // mockk
    testImplementation("io.mockk:mockk-android:1.13.7")
    // bytebuddy
    testImplementation("net.bytebuddy:byte-buddy:1.14.8")
    // coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    //
    testImplementation("app.cash.turbine:turbine:0.12.1")
    // truth
    testImplementation( "com.google.truth:truth:1.1.3")





}

apollo {
    service("service") {
        packageName.set("com.example.enturcase")
    }
}