plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.4"
}

dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
    implementation(project(":common"))
}

tasks {

    build {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21));
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}