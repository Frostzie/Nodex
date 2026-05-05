package io.github.frostzie.nodex.bootstrap.preLaunch

import java.io.InputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URI
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.security.MessageDigest

import io.github.frostzie.nodex.utils.LoggerProvider

/**
 * Downloads a single JavaFX classifier jar from Maven Central and verifies
 * its checksum before saving it to the cache.
 */
object MavenDownloader {

    private const val MAVEN_CENTRAL = "https://repo.maven.apache.org/maven2/org/openjfx"

    internal val logger = LoggerProvider.getLogger("MavenDownloader")

    internal enum class ChecksumAlgorithm(val digestName: String, val hexLength: Int, val urlSuffix: String) {
        SHA1("SHA-1", 40, ".sha1"),
    }

    internal data class RemoteChecksum(val algorithm: ChecksumAlgorithm, val hex: String, val url: String)

    /**
     * Downloads [artifactId]-[version]-[classifier].jar from Maven Central to
     * catch, verifying its checksum before saving the file.
     *
     * @throws IllegalStateException if the checksum does not match.
     * @throws IOException if any network or file operation fails.
     */
    fun downloadAndVerify(
        artifactId: String,
        version: String,
        classifier: String,
        destination: Path
    ) {
        val jarUrl = buildJarUrl(artifactId, version, classifier)

        val tempFile = Files.createTempFile(
            destination.parent,
            ".${artifactId}-${version}-${classifier}.",
            ".download"
        )

        try {
            openStream(jarUrl).use { input ->
                Files.newOutputStream(tempFile).use { output ->
                    copyStream(input, output)
                }
            }

            val expected = fetchBestChecksum(jarUrl)
            val actualHash = computeDigestHex(tempFile, expected.algorithm)

            if (!expected.hex.equals(actualHash, ignoreCase = true)) {
                error(
                    "${expected.algorithm.digestName} mismatch for $artifactId-$version-$classifier.jar\n" +
                            "expected : ${expected.hex}\n" +
                            "actual   : $actualHash\n" +
                            "checksum : ${expected.url}\n"
                )
            }

            Files.move(tempFile, destination, StandardCopyOption.REPLACE_EXISTING)

        } catch (e: Exception) {
            Files.deleteIfExists(tempFile)
            logger.error("Download failed for $artifactId-$version-$classifier: ${e.message}")
            throw e
        }
    }

    internal fun buildJarUrl(
        artifactId: String,
        version: String,
        classifier: String
    ): String {
        val fileName = "$artifactId-$version-$classifier.jar"
        return "$MAVEN_CENTRAL/$artifactId/$version/$fileName"
    }

    /**
     * Fetches the SHA-1 checksum for [jarUrl] from Maven Central.
     */
    internal fun fetchBestChecksum(
        jarUrl: String,
        open: (String) -> InputStream = ::openStream
    ): RemoteChecksum {
        val algorithm = ChecksumAlgorithm.SHA1
        val url = jarUrl + algorithm.urlSuffix
        try {
            val raw = open(url).bufferedReader().use { it.readText() }
            val hex = raw.trim().split(Regex("\\s+")).first().lowercase()
            if (!hex.matches(Regex("^[0-9a-f]{${algorithm.hexLength}}$"))) {
                throw IllegalStateException("Invalid ${algorithm.digestName} checksum content from $url: '$hex'")
            }
            return RemoteChecksum(algorithm = algorithm, hex = hex, url = url)
        } catch (e: IOException) {
            throw IllegalStateException("Failed to fetch SHA-1 checksum for $jarUrl", e)
        }
    }

    internal fun computeSha1(path: Path): String =
        computeDigestHex(path, ChecksumAlgorithm.SHA1)

    internal fun computeDigestHex(path: Path, algorithm: ChecksumAlgorithm): String {
        val digest = MessageDigest.getInstance(algorithm.digestName)
        Files.newInputStream(path).use { input ->
            val buffer = ByteArray(8192)
            while (true) {
                val bytesRead = input.read(buffer)
                if (bytesRead <= 0) break
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it.toInt() and 0xff) }
    }

    internal fun openStream(url: String): InputStream {
        val connection = URI.create(url).toURL().openConnection() as URLConnection
        connection.connectTimeout = 15000
        connection.readTimeout = 30000
        return connection.inputStream
    }

    private fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } > 0) {
            output.write(buffer, 0, bytesRead)
        }
    }
}
