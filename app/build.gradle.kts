plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace    = "com.sugarcare.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sugarcare.app"
        minSdk        = 34 // was 26 -> changed for signInWithGoogle/AuthManager
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0"
    }

    buildFeatures { compose = true }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    // Data Store
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    // Firebase & Google Credentials
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("co.yml:ycharts:2.1.0")
    implementation("com.google.firebase:firebase-firestore-ktx")


    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)

    // Core Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Activity & Lifecycle
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.core:core-ktx:1.15.0")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")


}

kotlin {
    jvmToolchain(21)
}
