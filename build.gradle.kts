import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("maven-publish")
    id("io.github.goooler.shadow") version "8.1.7" // Temp shadow repo with fix
    //id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    //id("io.papermc.hangar-publish-plugin") version "0.1.1"
    id("xyz.jpenilla.run-paper") version "2.0.0"
    id("io.papermc.paperweight.userdev") version "1.7.1" apply false
}

group = "me.lojosho"
version = "0.5.0"

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")

        // UpdateChecker
        maven("https://repo.jeff-media.com/public/")

        // Geary
        maven("https://repo.mineinabyss.com/releases/")
        maven("https://repo.mineinabyss.com/snapshots/")

        // Citizens & Denizen
        maven("https://maven.citizensnpcs.co/repo")

        // Jitpack
        maven("https://jitpack.io")

        // md-5 Repo
        maven("https://repo.md-5.net/content/groups/public/")

        // MMOItems
        maven("https://nexus.phoenixdevt.fr/repository/maven-public/")

        // PacketEvents
        maven("https://repo.codemc.io/repository/maven-releases/")

        // Eco-Suite/Auxilor Repo
        maven("https://repo.auxilor.io/repository/maven-public/")

        // PlaceholderAPI
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        // MythicMobs
        maven {
            url = uri("https://mvn.lumine.io/repository/maven-public")
            metadataSources {
                artifact()
            }
        }
    }

    dependencies {
        compileOnly(fileTree("${project.rootDir}/lib") { include("*.jar") })

        // Included externally
        compileOnly("com.mojang:authlib:1.5.25")
        compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
        compileOnly("org.jetbrains:annotations:24.1.0")
        compileOnly("com.github.oraxen:oraxen:1.160.0")
        compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
        compileOnly("com.mineinabyss:geary-papermc:0.27.0")
        compileOnly("it.unimi.dsi:fastutil:8.5.13")
        compileOnly("com.denizenscript:denizen:1.2.7-SNAPSHOT")
        compileOnly("io.lumine:Mythic-Dist:5.2.1")
        compileOnly("com.github.LeonMangler:SuperVanish:6.2.17")
        compileOnly("net.Indyuce:MMOItems-API:6.9.4-SNAPSHOT")
        compileOnly("com.willfp:eco:6.67.2")
        compileOnly("com.github.retrooper:packetevents-spigot:2.4.0")
        compileOnly("me.clip:placeholderapi:2.11.6")
        compileOnly("LibsDisguises:LibsDisguises:10.0.21") {
            exclude("org.spigotmc", "spigot")
        }
        compileOnly("com.github.Xiao-MoMi:Custom-Fishing:2.2.20")
        compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.2")

        // Lombok <3
        annotationProcessor("org.projectlombok:lombok:1.18.34")
        testCompileOnly("org.projectlombok:lombok:1.18.34")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

        // Spigot Auto Loader Libraries
        compileOnly("net.kyori:adventure-api:4.17.0")
        compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
        compileOnly("net.kyori:adventure-platform-bukkit:4.3.3")
        compileOnly("org.apache.commons:commons-lang3:3.14.0")

        // Shaded Dependencies
        implementation("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
        implementation("org.bstats:bstats-bukkit:3.0.2")
        implementation("com.jeff_media:SpigotUpdateChecker:3.0.0")
        implementation("com.github.BG-Software-LLC:CommentedConfiguration:bed3c46369")
    }
}

dependencies {
    implementation(project(path = ":common"))
    implementation(project(path = ":v1_19_R3", configuration = "reobf"))
    implementation(project(path = ":v1_20_R1", configuration = "reobf"))
    implementation(project(path = ":v1_20_R2", configuration = "reobf"))
    implementation(project(path = ":v1_20_R3", configuration = "reobf"))
    implementation(project(path = ":v1_20_R4", configuration = "reobf"))
    implementation(project(path = ":v1_21_R1", configuration = "reobf"))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        //options.release.set(17)
    }

    runServer {
        dependsOn(shadowJar)
        minecraftVersion("1.20.6")
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        dependsOn(":v1_19_R3:reobfJar")
        dependsOn(":v1_20_R1:reobfJar")
        dependsOn(":v1_20_R2:reobfJar")
        dependsOn(":v1_20_R3:reobfJar")
        dependsOn(":v1_20_R4:reobfJar")
        dependsOn(":v1_21_R1:reobfJar")
        mergeServiceFiles()

        relocate("org.bstats", "me.lojosho.shaded.bstats")
        relocate("org.spongepowered.configurate", "me.lojosho.shaded.configurate")
        relocate("com.jeff_media.updatechecker", "me.lojosho.shaded.updatechecker")
        relocate("com.bgsoftware", "me.lojosho.shaded.configupdater")

        dependencies {
            exclude(dependency("org.yaml:snakeyaml"))
        }

        archiveFileName.set("${project.name}-${project.version}.jar")

        doLast {
            archiveFile.get().asFile.copyTo(layout.projectDirectory.file("output/${project.name}-${project.version}.jar").asFile, true)
        }
    }

    build {
        dependsOn(shadowJar)
    }
}

// Handles generating the plugin yml

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    main = "me.lojosho.hibiscuscommons.HibiscusCommonsPlugin"
    apiVersion = "1.19"
    authors = listOf("LoJoSho")
    depend = listOf("PacketEvents")
    softDepend = listOf(
        "ModelEngine",
        "Oraxen",
        "ItemsAdder",
        "Geary",
        "HMCColor",
        "WorldGuard",
        "MythicMobs",
        "PlaceholderAPI",
        "SuperVanish",
        "PremiumVanish",
        "LibsDisguises",
        "Denizen",
        "MMOItems",
        "Eco"
    )
    version = "${project.version}"
    loadBefore = listOf(
        "Cosmin" // Fixes an issue with Cosmin loading before and taking /cosmetic, when messing with what we do.
    )

    libraries = listOf(
        "net.kyori:adventure-api:4.17.0",
        "net.kyori:adventure-text-minimessage:4.17.0",
        "net.kyori:adventure-platform-bukkit:4.3.3",
        "org.apache.commons:commons-lang3:3.14.0"
        //"org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT" // Readd when 4.2.0 releases
    )
}
/*
hangarPublish {
    publications.register("plugin") {
        version.set(project.version as String)
        channel.set("Release")
        if (project.version.toString().contains("-")) channel.set("Snapshot") // If its a dev build, it will have -dev on it
        id.set("HibiscusCommons")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.jar.flatMap { it.archiveFile })

                val versions: List<String> = listOf("1.18.2-1.20.4")
                platformVersions.set(versions)

                dependencies {
                    hangar("ProtocolLib") {
                        required.set(true)
                    }
                }
            }
        }
    }
}
*/
// Publishing stuff below here to a remote maven repo

publishing {
    val publishData = PublishData(project)
    publications {
        create<MavenPublication>("maven") {
            groupId = "${rootProject.group}"
            artifactId = "${rootProject.name}"
            version = "${rootProject.version}"

            from(components["java"])
        }
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("REPO_USERNAME")
                    password = System.getenv("REPO_PASSWORD")
                }
            }

            name = "HibiscusMCRepository"
            url = uri(publishData.getRepository())
        }
    }
}

class PublishData(private val project: Project) {
    var type: Type = getReleaseType()
    var hashLength: Int = 7

    private fun getReleaseType(): Type {
        val version = "${project.version}"
        return when {
            version.contains("dev") -> Type.DEV
            version.contains("SNAPSHOT") -> Type.SNAPSHOT
            else -> Type.RELEASE
        }
    }

    private fun getCheckedOutGitCommitHash(): String =
        System.getenv("GITHUB_SHA")?.substring(0, hashLength) ?: "local"

    private fun getCheckedOutBranch(): String =
        System.getenv("GITHUB_REF")?.replace("refs/heads/", "") ?: "local"

    fun getVersion(): String = getVersion(false)

    fun getVersion(appendCommit: Boolean): String =
        type.append(getVersionString(), appendCommit, getCheckedOutGitCommitHash())

    private fun getVersionString(): String =
        (rootProject.version as String).replace("-SNAPSHOT", "").replace("-DEV", "")

    fun getRepository(): String = type.repo

    enum class Type(private val append: String, val repo: String, private val addCommit: Boolean) {
        RELEASE("", "https://repo.hibiscusmc.com/releases/", false),
        DEV("", "https://repo.hibiscusmc.com/development/", true),
        SNAPSHOT("", "https://repo.hibiscusmc.com/snapshots/", true);

        fun append(name: String, appendCommit: Boolean, commitHash: String): String =
            name.plus(append).plus(if (appendCommit && addCommit) "-".plus(commitHash) else "")
    }
}