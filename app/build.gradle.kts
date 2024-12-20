import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val apkFileName = "ClockWidget.apk"
val currentVersionDate: Int = SimpleDateFormat("yyMMdd").format(Date()).toInt()
val currentVersion: String = SimpleDateFormat("yy.MM.dd").format(Date())

android {
    namespace = "la.shiro.widget"
    compileSdk = 34

    defaultConfig {
        applicationId = "la.shiro.widget"
        minSdk = 29
        targetSdk = 34
        versionCode = currentVersionDate
        versionName = currentVersion
    }
    signingConfigs {
        getByName("debug") {
            keyAlias = "android"
            keyPassword = "android"
            storeFile = file("jzhk.keystore")
            storePassword = "android"
        }
        create("release") {
            keyAlias = "android"
            keyPassword = "android"
            storeFile = file("jzhk.keystore")
            storePassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            isShrinkResources = true
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    applicationVariants.all {
        outputs.all {
            if (this is ApkVariantOutputImpl) {
                outputFileName = apkFileName
            }
        }
    }
}

allprojects {
    gradle.projectsEvaluated {
        tasks.register<Zip>("zipReleaseApkAndAssets") {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
            val date = dateFormat.format(Date())
            val apkFile = file("release/$apkFileName")
            val outputDir = file("dist")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            from(apkFile) {
                into("ClockWidget")
            }
            from("etc") {
                into("ClockWidget")
            }
            from("../ReadMe.md") {
                into("ClockWidget")
            }
            archiveFileName.set("ClockWidget_${date}.zip")
            destinationDirectory.set(outputDir)

            doLast {
                println("ZIP file created at: ${outputDir.absolutePath}/${archiveFileName.get()}")
            }
        }

        tasks.getByName("assembleRelease").finalizedBy("zipReleaseApkAndAssets")
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
}