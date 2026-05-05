import kotlin.String
import org.gradle.internal.os.OperatingSystem

plugins {
    id("net.fabricmc.fabric-loom")
    java
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.javafx)
}

val mcVersion = property("mcVersion")!!.toString()

javafx {
    version = libs.versions.javafxapp.get()
    modules("javafx.base", "javafx.graphics", "javafx.controls", "javafx.media", "javafx.fxml")
}

// Honestly made with AI I am way too tired to deal with gradle rn. If you find a better way, change this lol.
val os = OperatingSystem.current()!!
configurations.matching { it.isCanBeResolved }.all {
    attributes {
        attribute(
            OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE,
            objects.named(
                OperatingSystemFamily::class.java, when {
                    os.isWindows -> OperatingSystemFamily.WINDOWS
                    os.isMacOsX -> OperatingSystemFamily.MACOS
                    os.isLinux -> OperatingSystemFamily.LINUX
                    else -> error("Unsupported OS: $os")
                }
            )
        )
        attribute(
            MachineArchitecture.ARCHITECTURE_ATTRIBUTE,
            objects.named(
                MachineArchitecture::class.java, when (System.getProperty("os.arch")) {
                    "aarch64", "arm64" -> MachineArchitecture.ARM64
                    else -> MachineArchitecture.X86_64
                }
            )
        )
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
    }
}

// Naming examples
// Version Name: 0.0.1-fabric+mc1.20.5-1.21.8
// Jar Name: Nodex-0.0.1-fabric+mc1.20.5-1.21.8.jar
version = "${property("mod.version")}-fabric+mc${property("mod.mc_targets").toString().replace(" ", "-")}"
group = project.findProperty("maven_group") as String

base.archivesName = property("mod.id") as String

repositories {
    mavenCentral()
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    mavenLocal()
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    implementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    implementation(libs.fabric.kotlin)
    runtimeOnly(libs.devauth)

    // Style and icon packs
    implementation(libs.atlantaFX)
    implementation(libs.ikonliJavaFX)
    implementation(libs.ikonliCore)
    implementation(libs.material2)
    implementation(libs.feather)
    implementation(libs.jsvg)

    include(libs.atlantaFX)
    include(libs.ikonliJavaFX)
    include(libs.ikonliCore)
    include(libs.material2)
    include(libs.feather)
    include(libs.jsvg)

    // RichTextFX and it's dependencies
    implementation(libs.richTextFX)
    implementation(libs.flowless)
    implementation(libs.reactFX)
    implementation(libs.undofx)
    implementation(libs.wellbehavedfx)

    include(libs.richTextFX)
    include(libs.flowless)
    include(libs.reactFX)
    include(libs.undofx)
    include(libs.wellbehavedfx)

    // Jackson //TODO: Update to 3.x
    implementation(libs.jacksonCore)
    implementation(libs.jacksonDatabind)
    implementation(libs.jacksonModuleKotlin)
    implementation(libs.jacksonDatatypeJsr310)
    implementation(libs.jacksonAnnotations)

    include(libs.jacksonCore)
    include(libs.jacksonDatabind)
    include(libs.jacksonModuleKotlin)
    include(libs.jacksonDatatypeJsr310)
    include(libs.jacksonAnnotations)

    // File Watcher
    implementation(libs.directoryWatcher)
    include(libs.directoryWatcher)

    implementation(libs.koin.core)
    include(libs.koin.core)
}

// Taken from Stonecutter template
loom {
    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true") // Exports transformed classes for debugging
        runDir = "../../run" // Shares the run directory between versions
    }
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("id", project.findProperty("mod.id"))
    inputs.property("name", project.findProperty("mod.name"))
    inputs.property("version", project.findProperty("mod.version"))
    inputs.property("minecraft", project.findProperty("mod.mc_dep"))

    val props = mapOf(
        "id" to project.property("mod.id"),
        "name" to project.property("mod.name"),
        "version" to project.property("mod.version"),
        "minecraft" to project.property("mod.mc_dep")
    )

    filesMatching("fabric.mod.json") { expand(props) }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    jvmToolchain(25)
}

tasks.named<Jar>("jar") {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${project.findProperty("archives_base_name")}" }
    }
    from(rootProject.file("docs")) {
        into("docs")
        exclude("README_Pictures")
    }
    exclude("module-info.class")
    exclude("**/module-info.class")
    exclude("META-INF/MANIFEST.MF")
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named("jar") {
    doLast {
        copy {
            from(outputs.files)
            into(rootProject.file("build/libs"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.findProperty("archives_base_name") as String
            from(components["java"])
        }
    }
    repositories {

    }
}