import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.4"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("io.papermc.paperweight.userdev") apply false
    //id("io.papermc.hangar-publish-plugin") version "0.1.1"
}

group = "me.lojosho"
version = "0.9.0"

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    repositories {
        // Paper Repo
        maven("https://repo.papermc.io/repository/maven-public/")

        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")

        // Nexo
        maven("https://repo.nexomc.com/snapshots/")
        maven("https://repo.nexomc.com/releases/")

        // Geary repo
        maven("https://repo.mineinabyss.com/releases/")
        maven("https://repo.mineinabyss.com/snapshots/")

        // Citizens & Denizen
        maven("https://maven.citizensnpcs.co/repo")

        // Jitpack
        maven("https://jitpack.io")

        // md-5 Repo
        maven("https://repo.md-5.net/content/groups/public/")

        //LibsDisguises
        maven("https://mvn.lib.co.nz/public")

        // MMOItems
        maven("https://nexus.phoenixdevt.fr/repository/maven-public/")

        // Eco-Suite/Auxilor Repo
        maven("https://repo.auxilor.io/repository/maven-public/")

        // PlaceholderAPI
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        // Oraxen
        maven("https://repo.oraxen.com/releases")

        // Craft Engine
        maven("https://repo.momirealms.net/releases/")

        // Needed for brigadier for dependencies (I
        maven("https://libraries.minecraft.net/")

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
        compileOnly("com.mojang:authlib:3.13.56")
        compileOnly("org.jetbrains:annotations:26.0.1")
        compileOnly("io.th0rgal:oraxen:1.182.0")
        compileOnly("com.nexomc:nexo:1.4.0")
        compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.3-beta-14")
        compileOnly("com.mineinabyss:geary-papermc:0.31.0-dev.4")
        compileOnly("it.unimi.dsi:fastutil:8.5.15")
        compileOnly("com.denizenscript:denizen:1.2.7-SNAPSHOT")
        compileOnly("io.lumine:Mythic-Dist:5.8.0")
        compileOnly("com.github.LeonMangler:SuperVanish:6.2.17")
        compileOnly("net.Indyuce:MMOItems-API:6.9.4-SNAPSHOT")
        compileOnly("com.willfp:eco:6.74.5")
        compileOnly("me.clip:placeholderapi:2.11.6")
        compileOnly("me.libraryaddict.disguises:libsdisguises:11.0.0") {
            exclude("org.spigotmc", "spigot")
        }
        compileOnly("com.github.Xiao-MoMi:Custom-Fishing:2.3.3")
        compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.2")
        compileOnly("org.joml:joml:1.10.8")
        compileOnly("com.google.guava:guava:33.4.0-jre") // Sometimes just not included in compile time???
        compileOnly("com.github.Gecolay.GSit:core:3.1.1")
        compileOnly("net.momirealms:craft-engine-core:0.0.49")
        compileOnly("net.momirealms:craft-engine-bukkit:0.0.49")

        // Lombok <3
        annotationProcessor("org.projectlombok:lombok:1.18.36")
        compileOnly("org.projectlombok:lombok:1.18.36")
        testCompileOnly("org.projectlombok:lombok:1.18.36")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.36")

        // Spigot Auto Loader Libraries
        compileOnly("org.apache.commons:commons-lang3:3.17.0")

        // Shaded Dependencies
        implementation("org.spongepowered:configurate-yaml:4.2.0") {
            exclude("net.kyori")
        }
        implementation("net.kyori:option:1.1.0")
        implementation("org.bstats:bstats-bukkit:3.1.0")
        implementation("com.github.BG-Software-LLC:CommentedConfiguration:bed3c46369")
        implementation(fileTree("${project.rootDir}/lib") { include("SpigotUpdateChecker.jar") })
    }
}

dependencies {
    implementation(project(path = ":common"))
    implementation(project(path = ":v1_21_R1", configuration = "reobf"))
    implementation(project(path = ":v1_21_R2", configuration = "reobf"))
    implementation(project(path = ":v1_21_R3", configuration = "reobf"))
    implementation(project(path = ":v1_21_R4", configuration = "reobf"))
    implementation(project(path = ":v1_21_R5", configuration = "reobf"))
    implementation(project(path = ":v1_21_R6", configuration = "reobf"))
    implementation(project(path = ":v1_21_R7", configuration = "reobf"))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }

    runServer {
        dependsOn(shadowJar)
        dependsOn(jar)
        minecraftVersion("1.21.8")

        downloadPlugins {
            hangar("PlaceholderAPI", "2.11.6")
            url("https://download.luckperms.net/1593/bukkit/loader/LuckPerms-Bukkit-5.5.8.jar")
        }
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        dependsOn(":v1_21_R1:reobfJar")
        dependsOn(":v1_21_R2:reobfJar")
        dependsOn(":v1_21_R3:reobfJar")
        dependsOn(":v1_21_R4:reobfJar")
        dependsOn(":v1_21_R5:reobfJar")
        dependsOn(":v1_21_R6:reobfJar")
        dependsOn(":v1_21_R7:reobfJar")
        mergeServiceFiles()

        relocate("org.bstats", "me.lojosho.shaded.bstats")
        relocate("org.spongepowered.configurate", "me.lojosho.shaded.configurate")
        relocate("com.jeff_media.updatechecker", "me.lojosho.shaded.updatechecker")
        relocate("com.bgsoftware", "me.lojosho.shaded.configupdater")
        relocate("net.kyori.option", "me.lojosho.shaded.option")

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
    apiVersion = "1.20"
    authors = listOf("LoJoSho")
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
        "Eco",
        "Nexo",
        "CraftEngine",
        "GSit"
    )
    version = "${project.version}"
    loadBefore = listOf(
        "Cosmin" // Fixes an issue with Cosmin loading before and taking /cosmetic, when messing with what we do.
    )

    libraries = listOf(
        /*
        "net.kyori:adventure-api:4.24.0",
        "net.kyori:adventure-text-minimessage:4.24.0",
        "net.kyori:adventure-text-serializer-gson:4.24.0",
        "net.kyori:adventure-platform-bukkit:4.4.1",
         */
        "org.apache.commons:commons-lang3:3.17.0"
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

fun getGitCommitHash(): String {
    var includeHash = true
    val includeHashVariable = System.getenv("HMCC_INCLUDE_HASH")

    if (!includeHashVariable.isNullOrEmpty()) includeHash = includeHashVariable.toBoolean()

    if (includeHash) {
        return try {
            val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
                .redirectErrorStream(true)
                .start()

            process.inputStream.bufferedReader().use { "-" + it.readLine().trim() }
        } catch (e: Exception) {
            "-unknown" // Fallback if Git is not available or an error occurs
        }
    }
    return ""
}