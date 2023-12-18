plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(projects.uistate)

                // navigation
                implementation("moe.tlaster:precompose:1.5.8")
                implementation("moe.tlaster:precompose-molecule:1.5.8")
                implementation("app.cash.molecule:molecule-runtime:1.3.1")
                // ui
                implementation("com.eygraber:compose-placeholder-material3:1.0.7")
                implementation("io.github.qdsfdhvh:image-loader:1.7.1")
                // network
                implementation("io.ktor:ktor-client-core:2.3.7")
                implementation("io.ktor:ktor-client-logging:2.3.7")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
            }
        }
        androidMain {
            dependencies {
                // network
                implementation("io.ktor:ktor-client-okhttp:2.3.7")
            }
        }
        iosMain {
            dependencies {
                // network
                implementation("io.ktor:ktor-client-darwin:2.3.7")
            }
        }
    }
    jvmToolchain(17)
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.seiko.uistate.demo.common"
    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
