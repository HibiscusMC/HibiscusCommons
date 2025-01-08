pluginManagement {
    val userdevVersion: String by settings
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
    plugins {
        id("io.papermc.paperweight.userdev") version userdevVersion
    }
}

rootProject.name = "HibiscusCommons"
include(
    "common",
    "v1_20_R3",
    "v1_20_R4",
    "v1_21_R1",
    "v1_21_R2",
    "v1_21_R3",
)