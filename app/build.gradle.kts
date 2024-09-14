import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.org.jlleitschuh.gradle.ktlint)
    alias(libs.plugins.io.gitlab.arturbosch.detekt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dagger.hilt.android.plugin)
}

android {
    namespace = "com.codingpit.pvpcplanner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.codingpit.pvpcplanner"
        minSdk = 24
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

ktlint {
    android.set(true)
    ignoreFailures.set(false)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.SARIF)
    }
}

detekt {
    toolVersion = "1.23.7"
    parallel = true
    config.setFrom("config/detekt/config.yml")
    buildUponDefaultConfig = true
    basePath = projectDir.absolutePath
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
    // Chats
    implementation(libs.vico.compose.m3)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.okhttp)
    implementation(libs.moshi)
    implementation(libs.okhttp3.interceptor)
    implementation(libs.moshi.kotlin)

    // Hilt and Dagger
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.engine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.kotlinx.coroutines.test)

    // Other  Test
    testImplementation(libs.mockk)
}

tasks.withType<Test> {
    useJUnitPlatform() // Esto es necesario para que JUnit 5 sea reconocido
}
