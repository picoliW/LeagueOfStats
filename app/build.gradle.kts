import java.util.Properties

repositories {
    google()
    mavenCentral()
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp") version ("1.9.0-1.0.13")
    id("jacoco")
}

repositories {
    google()
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.7"
}

android {
    buildTypes {
        debug {
            isTestCoverageEnabled = true
        }
    }
}

tasks.register("jacocoTestReport", JacocoReport::class.java) {
    dependsOn("connectedDebugAndroidTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    sourceDirectories.setFrom(files("src/main/java"))
    classDirectories.setFrom(
        fileTree(mapOf(
            "dir" to "$buildDir/tmp/kotlin-classes/debug",
            "excludes" to listOf("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*")
        ))
    )
    executionData.setFrom(fileTree(mapOf(
        "dir" to buildDir,
        "includes" to listOf("**/*.exec", "**/*.ec")
    )))

    doFirst {
        executionData.files.forEach { file ->
            println("JaCoCo execution data found: ${file.absolutePath}")
        }
    }
}



val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val googleApiKey: String = localProperties.getProperty("GOOGLE_API_KEY", "google_api_key")
val riotApiKey: String = localProperties.getProperty("RIOT_API_KEY", "riot_api_key")
val appiumServerIp: String = localProperties.getProperty("APPIUM_SERVER_IP", "appium_server_ip")


android {
    namespace = "com.example.lol"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lol"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "GOOGLE_API_KEY", "\"${googleApiKey}\"")
        buildConfigField("String", "RIOT_API_KEY", "\"${riotApiKey}\"")
        buildConfigField("String", "APPIUM_SERVER_IP", "\"${appiumServerIp}\"")
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/io.netty.versions.properties"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"

        }
    }
}

configurations.all {
    resolutionStrategy {
        force ("com.google.guava:guava:31.1-jre")
    }
}

dependencies {
    implementation("io.appium:java-client:8.5.1") {
        exclude(group = "org.seleniumhq.selenium", module = "selenium-api")
        exclude(group = "commons-logging", module = "commons-logging")
        exclude(group = "org.springframework", module = "spring-jcl")
        exclude(group = "com.google.guava")
    }
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.seleniumhq.selenium:selenium-api:4.13.0")
    implementation("org.seleniumhq.selenium:selenium-remote-driver:4.13.0")
    implementation("org.seleniumhq.selenium:selenium-support:4.13.0")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("com.google.cloud:google-cloud-translate:1.94.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("io.coil-kt:coil-compose:2.1.0")
    androidTestImplementation("org.mockito:mockito-android:5.0.0")
    debugImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    debugImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation(libs.junit)
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    implementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}