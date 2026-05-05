package io.github.frostzie.nodex.bootstrap.preLaunch

import io.github.frostzie.nodex.utils.OSUtils

/**
 * Maps the current OS and CPU architecture to the JavaFX classifier string.
 */
object PlatformDetector {

    data class Platform(
        val classifier: String,
        val osName: String,
        val osArch: String
    )

    /**
     * Detects the current platform and returns the corresponding JavaFX classifier.
     */
    fun detect(): Platform {
        val osName = System.getProperty("os.name")
        val osArch = System.getProperty("os.arch")
        val classifier = resolveClassifier(OSUtils.os, OSUtils.arch, osArch)

        return Platform(
            classifier = classifier,
            osName = osName,
            osArch = osArch
        )
    }

    internal fun resolveClassifier(
        os: OSUtils.OSType,
        arch: OSUtils.ArchType,
        rawArch: String
    ): String = when (os) {
        OSUtils.OSType.WINDOWS -> {
            "win"
        }

        OSUtils.OSType.MAC -> when (arch) {
            OSUtils.ArchType.ARM64 -> "mac-aarch64"
            OSUtils.ArchType.X86_64 -> "mac"
            OSUtils.ArchType.UNKNOWN -> error(
                "Unrecognised macOS architecture '$rawArch'. "
            )
        }

        OSUtils.OSType.LINUX -> when (arch) {
            OSUtils.ArchType.X86_64 -> "linux"
            OSUtils.ArchType.ARM64 -> "linux-aarch64"
            OSUtils.ArchType.UNKNOWN -> error(
                "Unrecognised Linux architecture '$rawArch'."
            )
        }
    }
}
