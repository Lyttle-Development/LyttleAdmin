import io.papermc.hangarpublishplugin.model.Platforms
import java.io.ByteArrayOutputStream

plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.org.xerial.sqlite.jdbc)
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.net.luckperms.api)
}

group = "com.lyttldev"
version = "1.1.1"
description = "LyttleAdmin"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

// Helper methods
fun executeGitCommand(vararg command: String): String {
    val byteOut = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", *command)
        standardOutput = byteOut
    }
    return byteOut.toString(Charsets.UTF_8.name()).trim()
}

fun latestCommitMessage(): String {
    return executeGitCommand("log", "-1", "--pretty=%B")
}

// Add -SNAPSHOT to the version if the channel is not Release
val versionString: String =  if (System.getenv("CHANNEL") == "Release") {
    version.toString()
} else {
    val versionPrefix = if (System.getenv("CHANNEL") == "Snapshot") {
        "SNAPSHOT"
    } else {
        "ALPHA"
    }

    if (System.getenv("GITHUB_RUN_NUMBER") != null) {
        "${version}-${versionPrefix}+${System.getenv("GITHUB_RUN_NUMBER")}"
    } else {
        "$version-${versionPrefix}"
    }
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("plugin.yml") {
        expand("projectVersion" to versionString)
    }
}

// Get the channel from the environment variable or default to Alpha
val envChannel = System.getenv("CHANNEL") ?: "Alpha"

// Get the latest commit message for the changelog
val changelogContent: String = latestCommitMessage()

// Log the version and channel
println("Version: $versionString")
println("Channel: $envChannel")

hangarPublish {
    publications.register("plugin") {
        version.set(versionString)
        channel.set(envChannel)
        changelog.set(changelogContent)
        id.set("LyttleAdmin")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.jar.flatMap { it.archiveFile })

                // Get platform versions from gradle.properties file
                val versions: List<String> = (property("paperVersion") as String)
                    .split(",")
                    .map { it.trim() }
                platformVersions.set(versions)
            }
        }
    }
}
