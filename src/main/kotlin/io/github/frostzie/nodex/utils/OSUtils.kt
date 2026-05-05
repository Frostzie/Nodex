package io.github.frostzie.nodex.utils

object OSUtils {
    enum class OSType { WINDOWS, LINUX, MAC }
    enum class ArchType { X86_64, ARM64, UNKNOWN }

    val os: OSType by lazy {
        val operSys = System.getProperty("os.name").lowercase()
        when {
            operSys.contains("win") -> OSType.WINDOWS
            operSys.contains("nux") -> OSType.LINUX
            operSys.contains("mac") -> OSType.MAC
            else -> error("Unsupported OS")
        }
    }

    // Not truly sure if this is the best way or if it even works fully but time will tell. At least it works on my machine...
    val arch: ArchType by lazy {
        val arch = System.getProperty("os.arch").lowercase()
        when {
            arch.contains("aarch64") -> ArchType.ARM64
            arch.contains("x86_64") || arch.contains("amd64") -> ArchType.X86_64
            else -> ArchType.UNKNOWN
        }
    }
}