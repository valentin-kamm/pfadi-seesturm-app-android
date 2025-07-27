import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.parcelize)
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "ch.seesturm.pfadiseesturm"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
        compose = true
    }

    defaultConfig {
        applicationId = "ch.seesturm.pfadiseesturm"
        minSdk = 26
        targetSdk = 36
        versionCode = 21
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["appAuthRedirectScheme"] = "https"
    }
    signingConfigs {
        create("release") {

            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")

            if (localPropertiesFile.exists()) {

                localProperties.load(FileInputStream(localPropertiesFile))

                storeFile = file(localProperties.getProperty("release.storeFile", ""))
                storePassword = localProperties.getProperty("release.storePassword", "")
                keyAlias = localProperties.getProperty("release.keyAlias", "")
                keyPassword = localProperties.getProperty("release.keyPassword", "")

            } else {
                println("WARNING: local.properties file not found. Release build might fail if signing details are missing.")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    implementation(libs.core.ktx)
    implementation(libs.androidx.core.i18n)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.activity.ktx)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.functions)
    implementation(libs.firebase.appcheck)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // my dependencies
    implementation(libs.ui)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.adaptive.navigation)
    implementation(libs.androidx.material3.adaptive.navigation.suite)

    // icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // network requests
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // downloading images
    implementation(libs.coil.compose)

    // glass effect
    implementation(libs.haze)
    implementation(libs.haze.materials)

    // in-app browser
    implementation(libs.androidx.browser)

    // data store
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.collections.immutable)

    // charts
    implementation(libs.compose)

    // app auth
    implementation(libs.appauth)

    // rich text editor
    implementation(libs.richeditor.compose)

    // html utils
    implementation(libs.jsoup)

    // location
    implementation(libs.play.services.location)

    // splash screen
    implementation(libs.androidx.core.splashscreen)

    // zooming on images
    implementation(libs.zoomable)
}