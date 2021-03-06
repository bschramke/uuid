import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform") version "1.3.31"
    id("org.jetbrains.dokka") version "0.9.18"
    id("maven-publish")
    id("signing")
}

repositories {
    mavenCentral()
    jcenter()
}

kotlin {
    targets {
        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }
        }
        js {
            compilations.all {
                kotlinOptions {
                    sourceMap = true
                    moduleKind = "umd"
                    metaInfo = true
                }
            }
        }
        macosX64("macos")
        iosX64("iosSim")
        iosArm64("iosDevice64")
        iosArm32("iosDevice32")
        mingwX64("mingw") {
            binaries.findExecutable("test", DEBUG)!!.linkerOpts = mutableListOf("-Wl,--subsystem,windows, -Bstatic -lbcrypt")
        }
        linuxX64("linux64")
        linuxArm32Hfp("linux32")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-js")
            }
        }

        val nix64MainSourceSets = listOf(
                "src/nativeMain/kotlin",
                "src/nix64Main/kotlin"
        )

        val nix32MainSourceSets = listOf(
                "src/nativeMain/kotlin",
                "src/nix32Main/kotlin"
        )

        val macosMain by getting { kotlin.srcDirs(nix64MainSourceSets) }
        val macosTest by getting { kotlin.srcDir("src/cocoaTest/kotlin") }
        val iosDevice64Main by getting  { kotlin.srcDirs(nix64MainSourceSets) }
        val iosDevice64Test by getting  { kotlin.srcDir("src/cocoaTest/kotlin") }
        val iosDevice32Main by getting  { kotlin.srcDirs(nix32MainSourceSets) }
        val iosDevice32Test by getting  { kotlin.srcDir("src/cocoaTest/kotlin") }
        val iosSimMain by getting { kotlin.srcDirs(nix64MainSourceSets) }
        val iosSimTest by getting { kotlin.srcDir("src/cocoaTest/kotlin") }
        val mingwMain by getting {
            kotlin.srcDirs(listOf(
                "src/nativeMain/kotlin",
                "src/mingwMain/kotlin"
            ))
        }
        val linux64Main by getting { kotlin.srcDirs(nix64MainSourceSets) }
        val linux32Main by getting { kotlin.srcDirs(nix32MainSourceSets) }
    }
}

val ktlintConfig by configurations.creating

dependencies {
    ktlintConfig("com.pinterest:ktlint:0.32.0")
}

val ktlint by tasks.registering(JavaExec::class) {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = ktlintConfig
    main = "com.pinterest.ktlint.Main"
    args = listOf("src/**/*.kt")
}

val ktlintformat by tasks.registering(JavaExec::class) {
    group = "formatting"
    description = "Fix Kotlin code style deviations."
    classpath = ktlintConfig
    main = "com.pinterest.ktlint.Main"
    args = listOf("-F", "src/**/*.kt")
}

tasks.getByName("check") {
    configure {
        dependsOn(ktlint)
    }
}

if (HostManager.hostIsMac) {
    val linkTestDebugExecutableIosSim by tasks.getting(KotlinNativeLink::class)
    val testIosSim by tasks.registering(Exec::class) {
        group = "verification"
        dependsOn(linkTestDebugExecutableIosSim)
        executable("xcrun")
        args = listOf(
            "simctl",
            "spawn",
            "iPad Air 2",
            linkTestDebugExecutableIosSim.outputFile.get()
        )
    }

    tasks.getByName("check") {
        configure {
            dependsOn(testIosSim)
        }
    }
}

apply(from = "publish.gradle")
