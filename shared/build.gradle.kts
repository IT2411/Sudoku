plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            // Pure Kotlin standard library
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}