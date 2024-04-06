plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("module.publication")
    id("org.jetbrains.kotlin.plugin.serialization")
    jacoco
}

jacoco {
    toolVersion = "0.8.12" // Specify the desired JaCoCo version
    reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    // Other platforms, such as iOS, Android, JS, etc.

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Platform independent libraries
            }
        }
        val commonTest by getting {
            dependencies {
                // Platform independent test libraries
            }
        }
        val jvmMain by getting {
            dependencies {
                // JVM specific libraries
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation("org.junit.jupiter:junit-jupiter:$junitVersion")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
            tasks.withType<Test> {
                useJUnitPlatform()
                testLogging {
                    events("passed", "skipped", "failed")
                }
                finalizedBy(tasks.withType(JacocoReport::class))
            }
        }
        val jsMain by getting {
            dependencies {
                // Libraries for JS platform
            }
        }
        val jsTest by getting {
            dependencies {
                // JS specific test dependencies
            }
        }
        tasks.register("jacocoTestReport", JacocoReport::class) {
            dependsOn(tasks.withType(Test::class))
            val coverageSourceDirs = arrayOf(
                "src/commonMain",
                "src/jvmMain"
            )

            val buildDirectory = layout.buildDirectory

            val classFiles = buildDirectory.dir("classes/kotlin/jvm").get().asFile
                .walkBottomUp()
                .toSet()

            classDirectories.setFrom(classFiles)
            sourceDirectories.setFrom(files(coverageSourceDirs))

            buildDirectory.files("jacoco/jvmTest.exec").let {
                executionData.setFrom(it)
            }

            reports {
                xml.required = true
                csv.required = true
                html.required = true
            }
        }
    }
}