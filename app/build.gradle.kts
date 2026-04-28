import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.android.application)
    kotlin("plugin.serialization") version "2.3.21"
    alias(libs.plugins.org.jlleitschuh.gradle.ktlint)
    alias(libs.plugins.io.gitlab.arturbosch.detekt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dagger.hilt.android.plugin)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.codingpit.pvpcplanner"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.codingpit.pvpcplanner"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // Credentials are injected via environment variables in CI.
            // For local release builds, set these variables in your shell
            // before running ./gradlew assembleRelease or bundleRelease.
            // Fastlane also injects them via android.injected.signing.*
            // Gradle properties, which takes precedence over this block.
            val keystorePath = System.getenv("KEYSTORE_PATH")
            val storePass = System.getenv("STORE_PASSWORD")
            val keyAl = System.getenv("KEY_ALIAS")
            val keyPass = System.getenv("KEY_PASSWORD")
            val signingValues = listOf(keystorePath, storePass, keyAl, keyPass)
            val hasAny = signingValues.any { !it.isNullOrBlank() }
            val hasAll = signingValues.all { !it.isNullOrBlank() }

            if (hasAny && !hasAll) {
                throw GradleException(
                    "Partial signing configuration detected. " +
                        "Set KEYSTORE_PATH, STORE_PASSWORD, KEY_ALIAS, and KEY_PASSWORD.",
                )
            }

            if (hasAll) {
                storeFile = file(keystorePath!!)
                storePassword = storePass
                keyAlias = keyAl
                keyPassword = keyPass
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("androidTest") {
            assets.srcDirs("$projectDir/schemas")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_reports")
        metricsDestination = layout.buildDirectory.dir("compose_metrics")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=full")
        jvmTarget = JvmTarget.JVM_17
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
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
    baseline = file("detekt-baseline.xml")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:21.0-rc-1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
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
    implementation(libs.androidx.graphics.shapes)
    implementation(libs.androidx.material.icons.extended.android)

    // Hilt and Dagger
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation(libs.kotlinx.datetime)

    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.work.runtime)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.kotlinx.serialization.json)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    // Other  Test
    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockk.android)

    // Screenshot Tests
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.robolectric)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.ui.test.junit4)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// Commented out JUnit Platform to use JUnit 4 for now
// tasks.withType<Test> {
//     useJUnitPlatform()
// }
