plugins {
    id("java")
    id("io.papermc.paperweight.userdev")
}

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")
    implementation(project(":common"))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}